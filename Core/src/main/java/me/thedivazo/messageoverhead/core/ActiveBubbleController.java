package me.thedivazo.messageoverhead.core;

import me.thedivazo.messageoverhead.annotation.MainThread;
import me.thedivazo.messageoverhead.core.component.*;
import me.thedivazo.messageoverhead.core.event.EventBus;
import me.thedivazo.messageoverhead.core.event.RemoveBubbleEvent;
import me.thedivazo.messageoverhead.core.render.RendererBubble;
import me.thedivazo.messageoverhead.core.render.capability.CapabilityContainer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@MainThread
final class ActiveBubbleController implements ActiveBubble, CapabilityContainer {
    private final UUID uuid = UUID.randomUUID();

    private final Message message;
    private long ageTicks = 0;

    private final Author author;
    private final RendererBubble renderer;
    private boolean markRemoved;

    private final DefaultComponentContainer components;
    private final EventBus eventBus;

    ActiveBubbleController(Message message, Author author, RendererBubble renderer, ComponentRegistry registry, EventBus eventBus) {
        this.message = message;
        this.author = author;
        this.renderer = renderer;
        this.components = new DefaultComponentContainer(registry, this);
        this.eventBus = eventBus;
    }

    @Override
    public Author author() {
        return author;
    }

    @Override
    public UUID id() {
        return uuid;
    }

    @Override
    public Message message() {
        return message;
    }

    @Override
    public long ageTicks() {
        return ageTicks;
    }

    @Override
    public boolean isRemove() {
        return markRemoved;
    }

    @Override
    public void remove() {
        if (markRemoved) return;
        markRemoved = true;
        components.detachAllAndClose();
        renderer.destroy();
        eventBus.post(new RemoveBubbleEvent(this));
    }

    void onTickEnd() {}

    @Override
    public ComponentContainer container() {
        return components;
    }

    @Override
    public CapabilityContainer capabilities() {
        return this;
    }

    void tick() {
        if (markRemoved) return;
        ageTicks++;
        components.tick();
    }

    @Override
    public @Nullable <T> T capabilityOrNull(Class<T> type) {
        return renderer.capabilityOrNull(type);
    }
}
