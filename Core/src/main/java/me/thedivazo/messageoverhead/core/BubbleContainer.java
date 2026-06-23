package me.thedivazo.messageoverhead.core;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

public class BubbleContainer {
    private final Map<UUID, ActiveBubble> bubblesByMessageUid = new LinkedHashMap<>();
    private final Map<UUID, ActiveBubble> lastBubblesByPlayerUid = new LinkedHashMap<>();
    private final Map<UUID, Set<ActiveBubble>> oldBubblesByPlayerUid = new LinkedHashMap<>();

    public @Nullable ActiveBubble put(ActiveBubble bubble) {
        Objects.requireNonNull(bubble, "bubble");
        pruneActiveIndexes();

        if (bubble.isRemove()) {
            return null;
        }

        UUID messageUid = bubble.id();
        UUID playerUid = playerUid(bubble);
        ActiveBubble previousByMessage = bubblesByMessageUid.get(messageUid);
        ActiveBubble previousLast = lastBubblesByPlayerUid.get(playerUid);
        boolean previousLastCanBecomeOld = previousLast != null && !previousLast.isRemove();

        if (previousByMessage != null && previousByMessage != bubble) {
            unregisterActive(previousByMessage);
        }

        bubblesByMessageUid.put(bubble.id(), bubble);

        if (previousLastCanBecomeOld && previousLast != bubble) {
            oldBubblesByPlayerUid
                    .computeIfAbsent(playerUid, ignored -> newWeakBubbleSet())
                    .add(previousLast);
        }

        lastBubblesByPlayerUid.put(playerUid(bubble), bubble);
        return bubble;
    }

    public @Nullable ActiveBubble get(UUID uid) {
        Objects.requireNonNull(uid, "uid");
        pruneActiveIndexes();
        return bubblesByMessageUid.get(uid);
    }

    public @Nullable ActiveBubble getBubble(UUID messageUid) {
        return get(messageUid);
    }

    public Map<UUID, ActiveBubble> getBubbles() {
        return getBubblesByMessageUid();
    }

    public Map<UUID, ActiveBubble> getBubblesByMessageUid() {
        pruneActiveIndexes();
        return Collections.unmodifiableMap(new LinkedHashMap<>(bubblesByMessageUid));
    }

    public @Nullable ActiveBubble getLastBubble(UUID playerUid) {
        Objects.requireNonNull(playerUid, "playerUid");
        pruneActiveIndexes();
        return lastBubblesByPlayerUid.get(playerUid);
    }

    public Map<UUID, ActiveBubble> getLastBubbles() {
        return getLastBubblesByPlayerUid();
    }

    public Map<UUID, ActiveBubble> getLastBubblesByPlayerUid() {
        pruneActiveIndexes();
        return Collections.unmodifiableMap(new LinkedHashMap<>(lastBubblesByPlayerUid));
    }

    public Collection<ActiveBubble> getOldBubbles(UUID playerUid) {
        Objects.requireNonNull(playerUid, "playerUid");
        Set<ActiveBubble> oldBubbles = oldBubblesByPlayerUid.get(playerUid);
        if (oldBubbles == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(oldBubbles));
    }

    public Map<UUID, Collection<ActiveBubble>> getOldBubbles() {
        return getOldBubblesByPlayerUid();
    }

    public Map<UUID, Collection<ActiveBubble>> getOldBubblesByPlayerUid() {
        Map<UUID, Collection<ActiveBubble>> snapshot = new LinkedHashMap<>();
        oldBubblesByPlayerUid.entrySet().removeIf(entry -> {
            Set<ActiveBubble> oldBubbles = entry.getValue();
            if (oldBubbles.isEmpty()) {
                return true;
            }
            snapshot.put(entry.getKey(), Collections.unmodifiableList(new ArrayList<>(oldBubbles)));
            return false;
        });
        return Collections.unmodifiableMap(snapshot);
    }

    public @Nullable ActiveBubble remove(UUID uid) {
        Objects.requireNonNull(uid, "uid");

        ActiveBubble removed = bubblesByMessageUid.remove(uid);
        if (removed != null) {
            unregisterActive(removed);
        }

        return removed;
    }

    public boolean contains(UUID uid) {
        return get(uid) != null;
    }

    public void clear() {
        bubblesByMessageUid.clear();
        lastBubblesByPlayerUid.clear();
        oldBubblesByPlayerUid.clear();
    }

    private void pruneActiveIndexes() {
        bubblesByMessageUid.entrySet().removeIf(entry -> {
            ActiveBubble bubble = entry.getValue();
            boolean active = !bubble.isRemove();
            if (!active) {
                removeLastBubbleIfSame(bubble);
            }
            return !active;
        });

        lastBubblesByPlayerUid.entrySet().removeIf(entry -> entry.getValue().isRemove());
    }

    private void unregisterActive(ActiveBubble bubble) {
        bubblesByMessageUid.remove(bubble.id(), bubble);
        removeLastBubbleIfSame(bubble);
    }

    private void removeLastBubbleIfSame(ActiveBubble bubble) {
        lastBubblesByPlayerUid.remove(playerUid(bubble), bubble);
    }

    private UUID playerUid(ActiveBubble bubble) {
        return Objects.requireNonNull(bubble.author().getUID(), "playerUid");
    }

    private static Set<ActiveBubble> newWeakBubbleSet() {
        return Collections.newSetFromMap(new WeakHashMap<>());
    }
}
