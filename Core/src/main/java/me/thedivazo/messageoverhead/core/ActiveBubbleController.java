package me.thedivazo.messageoverhead.core;

import me.thedivazo.messageoverhead.core.component.*;
import me.thedivazo.messageoverhead.core.render.RendererBubble;
import me.thedivazo.messageoverhead.core.render.capability.CapabilityContainer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

final class ActiveBubbleController implements ActiveBubble, ComponentContext, CapabilityContainer {
    private final UUID uuid = UUID.randomUUID();

    private final Message message;
    private long ageTicks = 0;

    private final AuthorBubble author;
    private final RendererBubble renderer;
    private boolean markRemoved;

    private final DefaultComponentContainer components;

    ActiveBubbleController(Message message, AuthorBubble author, RendererBubble renderer, ComponentRegistry registry) {
        this.message = message;
        this.author = author;
        this.renderer = renderer;
        this.components = new DefaultComponentContainer(registry, this);
    }

    @Override
    public AuthorBubble author() {
        return author;
    }

    @Override
    public UUID uuid() {
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
        onTickEnd();
        renderer.destroy();
        markRemoved = true;
    }

    @Override
    public ComponentContainer container() {
        return components;
    }

    void tick() {
        if (markRemoved) return;
        ageTicks++;
        components.tick();
    }

    void onTickEnd() {
    }

    @Override
    public ActiveBubble bubble() {
        return this;
    }

    @Override
    public CapabilityContainer capabilityContainer() {
        return this;
    }

    @Override
    public @Nullable <T> T capabilityOrNull(Class<T> type) {
        return renderer.capabilityOrNull(type);
    }
}
