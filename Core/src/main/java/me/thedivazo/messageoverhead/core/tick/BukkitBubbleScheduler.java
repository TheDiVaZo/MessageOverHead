package me.thedivazo.messageoverhead.core.tick;

import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.TickableActiveBubble;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class BukkitBubbleScheduler implements BubbleScheduler {
    private final Plugin plugin;
    private final long delay;
    private final long period;
    private final Map<UUID, ScheduledBubble> bubbles = new LinkedHashMap<>();

    public BukkitBubbleScheduler(Plugin plugin) {
        this(plugin, 0L, 1L);
    }

    public BukkitBubbleScheduler(Plugin plugin, long delay, long period) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        if (delay < 0L) {
            throw new IllegalArgumentException("delay cannot be negative");
        }
        if (period <= 0L) {
            throw new IllegalArgumentException("period must be positive");
        }
        this.delay = delay;
        this.period = period;
    }

    @Override
    public @Nullable ActiveBubble put(TickableActiveBubble tickable) {
        Objects.requireNonNull(tickable, "tickable");

        if (tickable.bubble().isRemove()) {
            return null;
        }

        UUID uid = tickable.bubble().uuid();
        ScheduledBubble existing = getSynced(uid);
        if (existing != null) {
            if (existing.tickable() == tickable) {
                return tickable.bubble();
            }
            remove(uid);
        }

        ScheduledBubble scheduledBubble = new ScheduledBubble(tickable);
        bubbles.put(uid, scheduledBubble);

        try {
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(
                    plugin,
                    () -> tick(uid, scheduledBubble),
                    delay,
                    period
            );
            scheduledBubble.task(task);
        } catch (RuntimeException | Error exception) {
            bubbles.remove(uid);
            throw exception;
        }

        return tickable.bubble();
    }

    @Override
    public @Nullable ActiveBubble get(UUID uid) {
        ScheduledBubble entry = getSynced(uid);
        if (entry == null) {
            return null;
        }
        return entry.tickable().bubble();
    }

    @Override
    public @Nullable ActiveBubble remove(UUID uid) {
        Objects.requireNonNull(uid, "uid");

        ScheduledBubble entry = bubbles.remove(uid);
        if (entry == null) {
            return null;
        }

        try {
            if (!entry.tickable().bubble().isRemove()) {
                entry.tickable().bubble().remove();
            }
        } finally {
            stop(entry, false);
        }

        return entry.tickable().bubble();
    }

    @Override
    public boolean contains(UUID uid) {
        return getSynced(uid) != null;
    }

    private @Nullable ScheduledBubble getSynced(UUID uid) {
        Objects.requireNonNull(uid, "uid");

        ScheduledBubble entry = bubbles.get(uid);
        if (entry == null) {
            return null;
        }

        if (entry.tickable().bubble().isRemove()) {
            stop(uid, entry, false);
            return null;
        }

        return entry;
    }

    private void tick(UUID uid, ScheduledBubble entry) {
        if (entry.isStopped()) {
            return;
        }

        ScheduledBubble current = bubbles.get(uid);
        if (current != entry) {
            stop(entry, false);
            return;
        }

        if (entry.tickable().bubble().isRemove()) {
            stop(uid, entry, false);
            return;
        }

        try {
            entry.tickable().tick();
        } catch (RuntimeException | Error exception) {
            stop(uid, entry, true);
            throw exception;
        }

        if (entry.tickable().bubble().isRemove()) {
            stop(uid, entry, false);
        }
    }

    private void stop(UUID uid, ScheduledBubble entry, boolean tickEnd) {
        ScheduledBubble current = bubbles.get(uid);
        if (current == entry) {
            bubbles.remove(uid);
        }
        stop(entry, tickEnd);
    }

    private void stop(ScheduledBubble entry, boolean tickEnd) {
        if (entry.isStopped()) {
            entry.cancel();
            return;
        }

        entry.stopped(true);

        try {
            if (tickEnd) {
                entry.tickable().onTickEnd();
            }
        } finally {
            entry.cancel();
        }
    }

    private static final class ScheduledBubble {
        private final TickableActiveBubble tickable;
        private BukkitTask task;
        private boolean stopped;

        private ScheduledBubble(TickableActiveBubble tickable) {
            this.tickable = tickable;
        }

        private TickableActiveBubble tickable() {
            return tickable;
        }

        private boolean isStopped() {
            return stopped;
        }

        private void stopped(boolean stopped) {
            this.stopped = stopped;
        }

        private void task(BukkitTask task) {
            this.task = task;
        }

        private void cancel() {
            if (task != null) {
                task.cancel();
            }
        }
    }
}
