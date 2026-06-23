package me.thedivazo.messageoverhead.profile;

import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.Author;
import me.thedivazo.messageoverhead.core.BubbleContainer;
import me.thedivazo.messageoverhead.core.Message;
import me.thedivazo.messageoverhead.core.tick.BubbleScheduler;
import me.thedivazo.messageoverhead.core.tick.SchedulableBubble;
import me.thedivazo.messageoverhead.util.Positionc;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class BubbleSpawnManagerImpl implements BubbleSpawnManager {
    private final BubbleContainer container;
    private final BubbleScheduler scheduler;

    public BubbleSpawnManagerImpl(
            BubbleContainer container,
            BubbleScheduler scheduler
    ) {
        this.container = Objects.requireNonNull(container, "container");
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
    }

    @Override
    public ActiveBubble spawnBubble(Message message, Author author, Positionc positionc, BubbleProfile profile) {
        SchedulableBubble schedulable = profile.bubbleFactory().createBubble(message, author, positionc);
        Objects.requireNonNull(schedulable, "schedulable");

        ActiveBubble bubble = Objects.requireNonNull(schedulable.bubble(), "bubble");

        try {
            ActiveBubble finalBubble = bubble;
            profile.componentFactories()
                    .forEach((key, factory) -> {
                        var component = finalBubble.container().attachUnchecked(key, factory);
                        if (component == null) {
                            throw new IllegalStateException(
                                    "Component " + key.id() + " was not attached to bubble " + finalBubble.id()
                            );
                        }
                    });
        } catch (RuntimeException | Error exception) {
            if (!bubble.isRemove()) {
                bubble.remove();
            }
            throw exception;
        }

        bubble = scheduler.put(schedulable);

        if (bubble == null) {
            if (!schedulable.bubble().isRemove()) {
                schedulable.bubble().remove();
            }
            throw new IllegalStateException("Created bubble cannot be scheduled");
        }

        ActiveBubble indexed = container.put(bubble);
        if (indexed == null) {
            scheduler.remove(bubble.id());
            throw new IllegalStateException("Scheduled bubble cannot be indexed");
        }

        return bubble;
    }

    @Override
    public @Nullable ActiveBubble getBubble(UUID uid) {
        return container.get(uid);
    }

    @Override
    public @Nullable ActiveBubble removeBubble(UUID uid) {
        ActiveBubble indexed = container.remove(uid);
        ActiveBubble scheduled = scheduler.remove(uid);

        if (scheduled != null) {
            return scheduled;
        }

        if (indexed != null && !indexed.isRemove()) {
            indexed.remove();
        }

        return indexed;
    }

    @Override
    public boolean containsBubble(UUID uid) {
        return container.contains(uid);
    }

    @Override
    public void clearBubbles() {
        for (UUID uid : Set.copyOf(container.getBubblesByBubbleId().keySet())) {
            removeBubble(uid);
        }
        scheduler.clear();
        container.clear();
    }

    @Override
    public void close() {
        try {
            clearBubbles();
        } finally {
            scheduler.close();
            container.clear();
        }
    }
}
