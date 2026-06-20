package me.thedivazo.messageoverhead.profile;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.AuthorBubble;
import me.thedivazo.messageoverhead.core.BubbleFactory;
import me.thedivazo.messageoverhead.core.Message;
import me.thedivazo.messageoverhead.core.TickableActiveBubble;
import me.thedivazo.messageoverhead.core.component.BubbleComponentFactory;
import me.thedivazo.messageoverhead.core.component.ComponentKey;
import me.thedivazo.messageoverhead.core.tick.BubbleScheduler;
import me.thedivazo.messageoverhead.util.Positionc;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class ImmutableBubbleManager implements BubbleManager {
    private final BubbleFactory factory;
    private final BubbleScheduler scheduler;
    private final Object2ObjectMap<ComponentKey<?>, BubbleComponentFactory<?>> factories;

    public ImmutableBubbleManager(
            BubbleFactory factory,
            BubbleScheduler scheduler,
            Map<ComponentKey<?>, BubbleComponentFactory<?>> factories
    ) {
        this.factory = Objects.requireNonNull(factory, "factory");
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
        this.factories = new Object2ObjectOpenHashMap<>(Objects.requireNonNull(factories, "factories"));
    }

    @Override
    public ActiveBubble spawnBubble(Message message, AuthorBubble author, Positionc positionc) {
        TickableActiveBubble tickable = factory.createBubble(message, author, positionc);
        Objects.requireNonNull(tickable, "tickable");

        ActiveBubble bubble = Objects.requireNonNull(tickable.bubble(), "bubble");

        try {
            ActiveBubble finalBubble = bubble;
            factories.forEach((key, factory) -> finalBubble.container().attachUnchecked(key, factory));
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

    @Override
    public @Nullable ActiveBubble getBubble(UUID uid) {
        return scheduler.get(uid);
    }

    @Override
    public boolean containsBubble(UUID uid) {
        return scheduler.contains(uid);
    }
}
