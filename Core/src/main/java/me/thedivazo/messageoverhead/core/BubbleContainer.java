package me.thedivazo.messageoverhead.core;

import me.thedivazo.messageoverhead.core.tick.BubbleScheduler;
import me.thedivazo.messageoverhead.core.tick.SchedulableBubble;
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

public class BubbleContainer implements BubbleScheduler {
    private final BubbleScheduler scheduler;
    private final Map<UUID, ActiveBubble> bubblesByMessageUid = new LinkedHashMap<>();
    private final Map<UUID, ActiveBubble> lastBubblesByPlayerUid = new LinkedHashMap<>();
    private final Map<UUID, Set<ActiveBubble>> oldBubblesByPlayerUid = new LinkedHashMap<>();

    public BubbleContainer(BubbleScheduler scheduler) {
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
    }

    @Override
    public @Nullable ActiveBubble put(SchedulableBubble schedulable) {
        Objects.requireNonNull(schedulable, "schedulable");
        pruneActiveIndexes();

        ActiveBubble candidate = Objects.requireNonNull(schedulable.bubble(), "bubble");
        UUID messageUid = candidate.uuid();
        UUID playerUid = playerUid(candidate);
        ActiveBubble previousByMessage = bubblesByMessageUid.get(messageUid);
        ActiveBubble previousLast = lastBubblesByPlayerUid.get(playerUid);
        boolean previousLastCanBecomeOld = previousLast != null && !previousLast.isRemove();

        ActiveBubble scheduled = scheduler.put(schedulable);
        if (scheduled == null) {
            return null;
        }

        if (previousByMessage != null && previousByMessage != scheduled) {
            unregisterActive(previousByMessage);
        }

        bubblesByMessageUid.put(scheduled.uuid(), scheduled);

        if (previousLastCanBecomeOld && previousLast != scheduled) {
            oldBubblesByPlayerUid
                    .computeIfAbsent(playerUid, ignored -> newWeakBubbleSet())
                    .add(previousLast);
        }

        lastBubblesByPlayerUid.put(playerUid(scheduled), scheduled);
        return scheduled;
    }

    @Override
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

    @Override
    public @Nullable ActiveBubble remove(UUID uid) {
        Objects.requireNonNull(uid, "uid");

        ActiveBubble indexed = bubblesByMessageUid.get(uid);
        ActiveBubble removed = scheduler.remove(uid);
        if (removed != null) {
            unregisterActive(removed);
        } else if (indexed != null) {
            unregisterActive(indexed);
        }

        return removed;
    }

    @Override
    public boolean contains(UUID uid) {
        return get(uid) != null;
    }

    @Override
    public void clear() {
        try {
            scheduler.clear();
        } finally {
            bubblesByMessageUid.clear();
            lastBubblesByPlayerUid.clear();
            oldBubblesByPlayerUid.clear();
        }
    }

    @Override
    public void close() {
        try {
            scheduler.close();
        } finally {
            bubblesByMessageUid.clear();
            lastBubblesByPlayerUid.clear();
            oldBubblesByPlayerUid.clear();
        }
    }

    private void pruneActiveIndexes() {
        bubblesByMessageUid.entrySet().removeIf(entry -> {
            ActiveBubble bubble = entry.getValue();
            boolean active = isScheduled(bubble);
            if (!active) {
                removeLastBubbleIfSame(bubble);
            }
            return !active;
        });

        lastBubblesByPlayerUid.entrySet().removeIf(entry -> !isScheduled(entry.getValue()));
    }

    private boolean isScheduled(ActiveBubble bubble) {
        return !bubble.isRemove() && scheduler.get(bubble.uuid()) == bubble;
    }

    private void unregisterActive(ActiveBubble bubble) {
        bubblesByMessageUid.remove(bubble.uuid(), bubble);
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
