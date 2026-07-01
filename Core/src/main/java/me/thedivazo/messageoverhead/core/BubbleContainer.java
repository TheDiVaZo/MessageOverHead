package me.thedivazo.messageoverhead.core;

import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import me.thedivazo.messageoverhead.annotation.MainThread;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@MainThread
public class BubbleContainer {
    private final Map<UUID, ActiveBubble> bubblesByBubbleId = new LinkedHashMap<>();
    private final Map<UUID, ActiveBubble> lastBubblesByPlayerUid = new LinkedHashMap<>();
    private final Map<UUID, Set<ActiveBubble>> oldBubblesByPlayerUid = new LinkedHashMap<>();

    public @Nullable ActiveBubble put(ActiveBubble bubble) {
        Objects.requireNonNull(bubble, "bubble");

        if (bubble.isRemove()) {
            return null;
        }

        UUID bubbleId = bubble.id();
        UUID playerUid = playerUid(bubble);
        ActiveBubble previousByBubbleId = bubblesByBubbleId.get(bubbleId);
        ActiveBubble previousLast = lastBubblesByPlayerUid.get(playerUid);
        boolean previousLastCanBecomeOld = previousLast != null && !previousLast.isRemove();

        if (previousByBubbleId != null && previousByBubbleId != bubble) {
            unregisterActive(previousByBubbleId);
        }

        bubblesByBubbleId.put(bubble.id(), bubble);

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
        ActiveBubble activeBubble = bubblesByBubbleId.get(uid);
        return activeBubble == null || activeBubble.isRemove() ? null : activeBubble;
    }

    public @Nullable ActiveBubble getBubble(UUID bubbleId) {
        return get(bubbleId);
    }

    public Map<UUID, ActiveBubble> getBubblesByBubbleId() {
        return MapsKt.filter(bubblesByBubbleId, entry -> !entry.getValue().isRemove());
    }

    public @Nullable ActiveBubble getLastBubble(UUID playerUid) {
        Objects.requireNonNull(playerUid, "playerUid");
        ActiveBubble activeBubble = lastBubblesByPlayerUid.get(playerUid);
        return activeBubble == null || activeBubble.isRemove() ? null : activeBubble;
    }

    public Set<ActiveBubble> getOldBubbles(UUID playerUid) {
        Objects.requireNonNull(playerUid, "playerUid");
        Set<ActiveBubble> oldBubbles = oldBubblesByPlayerUid.get(playerUid);
        if (oldBubbles == null) {
            return Collections.emptySet();
        }
        return CollectionsKt.filterTo(oldBubbles, new HashSet<>(), bubble -> !bubble.isRemove());
    }

    public Map<UUID, Collection<ActiveBubble>> getOldBubbles() {
        return getOldBubblesByPlayerUid();
    }

    public Map<UUID, Collection<ActiveBubble>> getOldBubblesByPlayerUid() {
        return MapsKt.mapValues(
                oldBubblesByPlayerUid,
                entry -> CollectionsKt.filter(entry.getValue(), bubble -> !bubble.isRemove())
        );
    }

    public @Nullable ActiveBubble remove(UUID uid) {
        Objects.requireNonNull(uid, "uid");

        ActiveBubble removed = bubblesByBubbleId.remove(uid);
        if (removed != null) {
            unregisterActive(removed);
        }

        return removed == null || removed.isRemove() ? null : removed;
    }

    public boolean contains(UUID uid) {
        return get(uid) != null;
    }

    public void clear() {
        bubblesByBubbleId.clear();
        lastBubblesByPlayerUid.clear();
        oldBubblesByPlayerUid.clear();
    }

    public void invalidate() {
        pruneActiveIndexes();
        pruneOldIndexes();
    }

    private void pruneActiveIndexes() {
        bubblesByBubbleId.entrySet().removeIf(entry -> {
            ActiveBubble bubble = entry.getValue();
            boolean active = !bubble.isRemove();
            if (!active) {
                removeLastBubbleIfSame(bubble);
            }
            return !active;
        });

        lastBubblesByPlayerUid.entrySet().removeIf(entry -> entry.getValue().isRemove());
    }

    private void pruneOldIndexes() {
        oldBubblesByPlayerUid.entrySet().removeIf(entry -> {
            Set<ActiveBubble> oldBubbles = entry.getValue();
            oldBubbles.removeIf(bubble -> bubble == null
                    || bubble.isRemove()
                    || !bubblesByBubbleId.containsKey(bubble.id()));
            return oldBubbles.isEmpty();
        });
    }

    private void unregisterActive(ActiveBubble bubble) {
        bubblesByBubbleId.remove(bubble.id(), bubble);
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
