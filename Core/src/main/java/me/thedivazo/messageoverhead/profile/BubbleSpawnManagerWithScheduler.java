package me.thedivazo.messageoverhead.profile;

import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.AuthorBubble;
import me.thedivazo.messageoverhead.core.Message;
import me.thedivazo.messageoverhead.core.tick.BubbleScheduler;
import me.thedivazo.messageoverhead.core.tick.SchedulableBubble;
import me.thedivazo.messageoverhead.util.Positionc;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public final class BubbleSpawnManagerWithScheduler implements BubbleSpawnManager {
    private final BubbleScheduler scheduler;

    public BubbleSpawnManagerWithScheduler(
            BubbleScheduler scheduler
    ) {
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
    }

    @Override
    public ActiveBubble spawnBubble(Message message, AuthorBubble author, Positionc positionc, BubbleProfile profile) {
        SchedulableBubble schedulable = profile.bubbleFactory().createBubble(message, author, positionc);
        Objects.requireNonNull(schedulable, "schedulable");

        ActiveBubble bubble = Objects.requireNonNull(schedulable.bubble(), "bubble");

        try {
            ActiveBubble finalBubble = bubble;
            profile.componentFactories()
                    .forEach(entry -> finalBubble.container().attachUnchecked(entry.key(), entry.factory()));
        } catch (RuntimeException | Error exception) {
            if (!bubble.isRemove()) {
                bubble.remove();
            }
            throw exception;
        }

        bubble = scheduler.put(schedulable);

        if (bubble == null) {
            throw new IllegalStateException("Created bubble cannot be scheduled");
        }

        return bubble;
    }

    @Override
    public @Nullable ActiveBubble getBubble(UUID uid) {
        return scheduler.get(uid);
    }

    @Override
    public @Nullable ActiveBubble removeBubble(UUID uid) {
        return scheduler.remove(uid);
    }

    @Override
    public boolean containsBubble(UUID uid) {
        return scheduler.contains(uid);
    }

    @Override
    public void clearBubbles() {
        scheduler.clear();
    }

    @Override
    public void close() {
        scheduler.close();
    }
}
