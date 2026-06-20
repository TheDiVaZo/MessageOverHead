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

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class MutableBubbleManager implements BubbleManager {
    private BubbleFactory factory;
    private final BubbleScheduler scheduler;
    private Map<ComponentKey<?>, BubbleComponentFactory<?>> factories;

    public MutableBubbleManager(
            BubbleFactory factory,
            BubbleScheduler scheduler,
            Map<ComponentKey<?>, BubbleComponentFactory<?>> factories
    ) {
        setFactory(factory);
        this.scheduler = scheduler;
        setFactories(factories);
    }

    public void setFactory(BubbleFactory factory) {
        this.factory = Objects.requireNonNull(factory, "factory");
    }

    public void setFactories(Map<ComponentKey<?>, BubbleComponentFactory<?>> factories) {
        this.factories = Collections.unmodifiableMap(new Object2ObjectOpenHashMap<>(Objects.requireNonNull(factories, "factories")));
    }

    public Map<ComponentKey<?>, BubbleComponentFactory<?>> getFactories() {
        return factories;
    }

    public void setFactories(Object2ObjectMap<ComponentKey<?>, BubbleComponentFactory<?>> factories) {
        this.factories = factories;
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
