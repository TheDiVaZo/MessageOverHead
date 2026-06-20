package me.thedivazo.messageoverhead.core;

import me.thedivazo.messageoverhead.core.component.ComponentRegistry;
import me.thedivazo.messageoverhead.core.render.RendererFactory;
import me.thedivazo.messageoverhead.util.Positionc;

public class DefaultBubbleFactory implements BubbleFactory {
    private final ComponentRegistry componentRegistry;
    private final RendererFactory rendererFactory;

    public DefaultBubbleFactory(ComponentRegistry componentRegistry, RendererFactory rendererFactory) {
        this.componentRegistry = componentRegistry;
        this.rendererFactory = rendererFactory;
    }

    @Override
    public TickableActiveBubble createBubble(Message message, AuthorBubble author, Positionc positionc) {
        return new ActiveBubbleController(message, author, rendererFactory.create(message, positionc), componentRegistry);
    }
}
