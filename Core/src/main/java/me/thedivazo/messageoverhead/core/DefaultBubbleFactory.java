package me.thedivazo.messageoverhead.core;

import me.thedivazo.messageoverhead.ComponentService;
import me.thedivazo.messageoverhead.core.render.RendererFactory;
import me.thedivazo.messageoverhead.util.Positionc;

public class DefaultBubbleFactory implements BubbleFactory {
    private final ComponentService componentService;
    private final RendererFactory rendererFactory;

    public DefaultBubbleFactory(ComponentService componentService, RendererFactory rendererFactory) {
        this.componentService = componentService;
        this.rendererFactory = rendererFactory;
    }

    @Override
    public ActiveBubble createBubble(Message message, AuthorBubble author, Positionc positionc) {
        return new ActiveBubbleController(message, author, rendererFactory.create(message, positionc), componentService);
    }
}
