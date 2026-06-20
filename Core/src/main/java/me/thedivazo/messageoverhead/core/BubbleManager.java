package me.thedivazo.messageoverhead.core;

import me.thedivazo.messageoverhead.core.tick.BubbleScheduler;
import me.thedivazo.messageoverhead.util.Positionc;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class BubbleManager {
    private final BubbleFactory factory;
    private final BubbleScheduler scheduler;
    private final List<Consumer<ActiveBubble>> preCreateCallbacks = new ArrayList<>();

    public BubbleManager(BubbleFactory factory, BubbleScheduler scheduler) {
        this.factory = Objects.requireNonNull(factory, "factory");
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
    }

    public ActiveBubble spawnBubble(Message message, AuthorBubble author, Positionc positionc) {
        TickableActiveBubble tickable = factory.createBubble(message, author, positionc);
        Objects.requireNonNull(tickable, "tickable");

        ActiveBubble bubble = Objects.requireNonNull(tickable.bubble(), "bubble");

        try {
            callPreCreateCallbacks(bubble);
        } catch (RuntimeException | Error exception) {
            if (!bubble.isRemove()) {
                bubble.remove();
            }
            throw exception;
        }

        bubble = scheduler.put(tickable);

        if (bubble == null) {
            throw new IllegalStateException("Created bubble cannot be scheduled");
        }

        return bubble;
    }

    public void addPreCreateCallback(Consumer<ActiveBubble> callback) {
        preCreateCallbacks.add(Objects.requireNonNull(callback, "callback"));
    }

    public boolean removePreCreateCallback(Consumer<ActiveBubble> callback) {
        Objects.requireNonNull(callback, "callback");
        return preCreateCallbacks.remove(callback);
    }

    public @Nullable ActiveBubble getBubble(UUID uid) {
        return scheduler.get(uid);
    }

    public boolean containsBubble(UUID uid) {
        return scheduler.contains(uid);
    }

    private void callPreCreateCallbacks(ActiveBubble bubble) {
        for (Consumer<ActiveBubble> callback : List.copyOf(preCreateCallbacks)) {
            callback.accept(bubble);
        }
    }
}
