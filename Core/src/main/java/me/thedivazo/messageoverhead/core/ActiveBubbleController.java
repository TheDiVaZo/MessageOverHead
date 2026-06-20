package me.thedivazo.messageoverhead.core;

import me.thedivazo.messageoverhead.ComponentService;
import me.thedivazo.messageoverhead.core.component.*;
import me.thedivazo.messageoverhead.core.render.RendererBubble;
import me.thedivazo.messageoverhead.core.render.capability.CapabilityContainer;
import me.thedivazo.messageoverhead.core.tick.StopReason;
import me.thedivazo.messageoverhead.core.tick.TickableObject;
import org.jetbrains.annotations.Nullable;

public class ActiveBubbleController implements TickableObject, ActiveBubble, ComponentContext, CapabilityContainer {
    private final Message message;
    private long ageTicks = 0;

    private final AuthorBubble author;
    private final RendererBubble renderer;
    private boolean markRemoved;

    private final DefaultComponentContainer components;

    public ActiveBubbleController(Message message, AuthorBubble author, RendererBubble renderer, ComponentService service) {
        this.message = message;
        this.author = author;
        this.renderer = renderer;
        this.components = new DefaultComponentContainer(service, this);
    }

    @Override
    public AuthorBubble author() {
        return author;
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
        onTickEnd(StopReason.BUBBLE_REMOVE);
        renderer.destroy();
        markRemoved = true;
    }

    @Override
    public ComponentContainer container() {
        return components;
    }

    @Override
    public void tick() {
        if (markRemoved) return;
        ageTicks++;
        components.tick();
    }

    @Override
    public void onTickEnd(StopReason stopReason) {
        if (markRemoved) return;
        components.detachAll();
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
