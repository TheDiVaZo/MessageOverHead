package me.thedivazo.messageoverhead.core;

import me.thedivazo.messageoverhead.core.component.BubbleComponentsContainer;
import me.thedivazo.messageoverhead.core.component.ComponentProvider;
import me.thedivazo.messageoverhead.core.component.ScopeComponentPosition;
import me.thedivazo.messageoverhead.core.component.ScopeComponentView;
import me.thedivazo.messageoverhead.core.render.RendererBubble;
import me.thedivazo.messageoverhead.core.render.capability.RendererPosition;
import me.thedivazo.messageoverhead.core.render.capability.RendererView;
import me.thedivazo.messageoverhead.core.tick.StopReason;
import me.thedivazo.messageoverhead.core.tick.TickableObject;
import org.jetbrains.annotations.Nullable;

public class ActiveBubbleController implements TickableObject, ActiveBubble, ComponentProvider {
    private final Message message;
    private long ageTicks = 0;

    private final AuthorBubble author;
    private RendererBubble renderer;
    private boolean markRemoved;

    private BubbleComponentsContainer components = new BubbleComponentsContainer(this);

    public ActiveBubbleController(AuthorBubble authorBubble, Message message, RendererBubble renderer) {
        this.message = message;
        this.renderer = renderer;
        this.author = authorBubble;

        if (renderer instanceof RendererPosition) {
            components.setComponent(ScopeComponentPosition.class, new ScopeComponentPosition(this, (RendererPosition) renderer));
        }
    }

    public boolean setViewComponent(ScopeComponentView.Settings settings) {
        if (renderer instanceof RendererView) {
            components.setComponent(ScopeComponentView.class, new ScopeComponentView(this, (RendererView) renderer, settings));
            return true;
        }
        return false;
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
        markRemoved = true;
    }

    @Override
    public <T> @Nullable T getComponent(Class<T> type) {
        return components.getComponent(type);
    }

    @Override
    public <T> boolean hasComponent(Class<T> type) {
        return components.hasComponent(type);
    }

    @Override
    public <T extends TickableObject> void setComponent(Class<T> clazz, T object) {
        components.setComponent(clazz, object);
    }

    @Override
    public void tick() {
        if (markRemoved) return;
        ageTicks++;
        components.tick();
    }

    @Override
    public void onTickStart() {
        if (markRemoved) return;
        components.onTickStart();
    }

    @Override
    public void onTickEnd(StopReason stopReason) {
        if (markRemoved) return;
        components.onTickEnd(stopReason);
    }
}
