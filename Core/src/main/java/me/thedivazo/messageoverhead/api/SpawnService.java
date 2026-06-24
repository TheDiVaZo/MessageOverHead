package me.thedivazo.messageoverhead.api;

import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.Author;
import me.thedivazo.messageoverhead.core.BubbleContainer;
import me.thedivazo.messageoverhead.core.Message;
import me.thedivazo.messageoverhead.core.component.BubbleComponentFactory;
import me.thedivazo.messageoverhead.core.component.ComponentKey;
import me.thedivazo.messageoverhead.core.component.ProfileComponent;
import me.thedivazo.messageoverhead.core.tick.BubbleScheduler;
import me.thedivazo.messageoverhead.core.tick.SchedulableBubble;
import me.thedivazo.messageoverhead.profile.BubbleProfile;
import me.thedivazo.messageoverhead.util.Positionc;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class SpawnService {
    private final BubbleContainer container;
    private final BubbleScheduler scheduler;

    public SpawnService(
            BubbleContainer container,
            BubbleScheduler scheduler
    ) {
        this.container = Objects.requireNonNull(container, "container");
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
    }

    public ActiveBubble spawnBubble(Message message, Author author, Positionc positionc, BubbleProfile profile) {
        SchedulableBubble schedulable = profile.bubbleFactory().createBubble(message, author, positionc);
        Objects.requireNonNull(schedulable, "schedulable");

        ActiveBubble bubble = Objects.requireNonNull(schedulable.bubble(), "bubble");
        ProfileComponent.attach(bubble, profile.id());

        try {
            bubble.container().attachGroup(profile.componentFactories());
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

    public @Nullable ActiveBubble getBubble(UUID uid) {
        return container.get(uid);
    }

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

    public boolean containsBubble(UUID uid) {
        return container.contains(uid);
    }

    public void clearBubbles() {
        for (UUID uid : Set.copyOf(container.getBubblesByBubbleId().keySet())) {
            removeBubble(uid);
        }
        scheduler.clear();
        container.clear();
    }

    public void close() {
        try {
            clearBubbles();
        } finally {
            scheduler.close();
            container.clear();
        }
    }
}
