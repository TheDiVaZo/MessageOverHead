package me.thedivazo.messageoverhead.core.tick;

import me.thedivazo.messageoverhead.annotation.MainThread;
import me.thedivazo.messageoverhead.core.ActiveBubble;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

@MainThread
public final class BukkitBubbleScheduler implements BubbleScheduler {
    private final Plugin plugin;
    private final long delay;
    private final long period;
    private final Map<UUID, SchedulableBubble> bubbles = new LinkedHashMap<>();
    private BukkitTask task;
    private boolean closed;

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
    public @Nullable ActiveBubble put(SchedulableBubble schedulable) {
        Objects.requireNonNull(schedulable, "schedulable");

        if (closed) {
            throw new IllegalStateException("Bubble scheduler is closed");
        }

        ActiveBubble bubble = schedulable.bubble();
        TickableObject tickable = schedulable.tickable();

        if (bubble.isRemove()) {
            return null;
        }

        UUID uid = bubble.id();
        SchedulableBubble existing = getSynced(uid);
        if (existing != null) {
            if (existing.bubble() == bubble && existing.tickable() == tickable) {
                return bubble;
            }
            remove(uid);
        }

        bubbles.put(uid, schedulable);

        try {
            ensureTask();
        } catch (RuntimeException | Error exception) {
            bubbles.remove(uid, schedulable);
            throw exception;
        }

        return bubble;
    }

    @Override
    public @Nullable ActiveBubble get(UUID uid) {
        if (closed) {
            return null;
        }

        SchedulableBubble entry = getSynced(uid);
        if (entry == null) {
            return null;
        }
        return entry.bubble();
    }

    @Override
    public @Nullable ActiveBubble remove(UUID uid) {
        Objects.requireNonNull(uid, "uid");

        if (closed) {
            return null;
        }

        SchedulableBubble entry = bubbles.remove(uid);
        if (entry == null) {
            return null;
        }

        try {
            if (!entry.bubble().isRemove()) {
                entry.bubble().remove();
            }
        } finally {
            stopTaskIfIdle();
        }

        return entry.bubble();
    }

    @Override
    public boolean contains(UUID uid) {
        if (closed) {
            return false;
        }

        return getSynced(uid) != null;
    }

    @Override
    public void clear() {
        clearEntries();
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }

        closed = true;
        clearEntries();
    }

    private void clearEntries() {
        List<SchedulableBubble> entries = new ArrayList<>(bubbles.values());
        bubbles.clear();

        RuntimeException runtimeFailure = null;
        Error errorFailure = null;

        for (SchedulableBubble entry : entries) {
            try {
                if (!entry.bubble().isRemove()) {
                    entry.bubble().remove();
                }
            } catch (RuntimeException exception) {
                if (runtimeFailure == null) {
                    runtimeFailure = exception;
                } else {
                    runtimeFailure.addSuppressed(exception);
                }
            } catch (Error exception) {
                if (errorFailure == null) {
                    errorFailure = exception;
                } else {
                    errorFailure.addSuppressed(exception);
                }
            }
        }

        cancelTask();

        if (errorFailure != null) {
            throw errorFailure;
        }
        if (runtimeFailure != null) {
            throw runtimeFailure;
        }
    }

    private @Nullable SchedulableBubble getSynced(UUID uid) {
        Objects.requireNonNull(uid, "uid");

        SchedulableBubble entry = bubbles.get(uid);
        if (entry == null) {
            return null;
        }

        if (entry.bubble().isRemove()) {
            bubbles.remove(uid, entry);
            stopTaskIfIdle();
            return null;
        }

        return entry;
    }

    private void ensureTask() {
        if (task != null && !task.isCancelled()) {
            return;
        }

        task = Bukkit.getScheduler().runTaskTimer(
                plugin,
                this::tickAll,
                delay,
                period
        );
    }

    private void tickAll() {
        if (closed) {
            cancelTask();
            return;
        }

        List<Map.Entry<UUID, SchedulableBubble>> entries = new ArrayList<>(bubbles.entrySet());
        for (Map.Entry<UUID, SchedulableBubble> entry : entries) {
            UUID uid = entry.getKey();
            try {
                tick(uid, entry.getValue());
            } catch (RuntimeException | Error exception) {
                plugin.getLogger().log(Level.SEVERE, "Failed to tick bubble " + uid, exception);
            }
        }

        stopTaskIfIdle();
    }

    private void tick(UUID uid, SchedulableBubble entry) {
        SchedulableBubble current = bubbles.get(uid);
        if (current != entry) {
            return;
        }

        if (entry.bubble().isRemove()) {
            bubbles.remove(uid, entry);
            stopTaskIfIdle();
            return;
        }

        try {
            entry.tickable().tick();
        } catch (RuntimeException | Error exception) {
            try {
                if (!entry.bubble().isRemove()) {
                    entry.bubble().remove();
                }
            } catch (RuntimeException | Error cleanupException) {
                exception.addSuppressed(cleanupException);
            }
            try {
                stop(uid, entry, true);
            } catch (RuntimeException | Error cleanupException) {
                exception.addSuppressed(cleanupException);
            }
            throw exception;
        }

        if (entry.bubble().isRemove()) {
            bubbles.remove(uid, entry);
            stopTaskIfIdle();
        }
    }

    private void stop(UUID uid, SchedulableBubble entry, boolean tickEnd) {
        SchedulableBubble current = bubbles.get(uid);
        if (current == entry) {
            bubbles.remove(uid);
        }

        if (tickEnd) {
            entry.tickable().onTickEnd();
        }
    }

    private void stopTaskIfIdle() {
        if (bubbles.isEmpty()) {
            cancelTask();
        }
    }

    private void cancelTask() {
        BukkitTask currentTask = task;
        task = null;

        if (currentTask != null) {
            currentTask.cancel();
        }
    }
}
