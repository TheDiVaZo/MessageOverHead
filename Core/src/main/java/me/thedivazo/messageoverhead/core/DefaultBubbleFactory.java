package me.thedivazo.messageoverhead.core;

import me.thedivazo.messageoverhead.core.component.ComponentRegistry;
import me.thedivazo.messageoverhead.core.render.RendererFactory;
import me.thedivazo.messageoverhead.core.tick.SchedulableBubble;
import me.thedivazo.messageoverhead.core.tick.TickableObject;
import me.thedivazo.messageoverhead.util.Positionc;

public class DefaultBubbleFactory implements BubbleFactory {
    private final ComponentRegistry componentRegistry;
    private final RendererFactory rendererFactory;

    public DefaultBubbleFactory(ComponentRegistry componentRegistry, RendererFactory rendererFactory) {
        this.componentRegistry = componentRegistry;
        this.rendererFactory = rendererFactory;
    }

    @Override
    public SchedulableBubble createBubble(Message message, Author author, Positionc positionc) {
        ActiveBubbleController bubble = new ActiveBubbleController(
                message,
                author,
                rendererFactory.create(message, positionc),
                componentRegistry
        );

        return new SchedulableBubble(bubble, new TickableObject() {
            @Override
            public void onTickEnd() {
                bubble.onTickEnd();
            }

            @Override
            public void tick() {
                bubble.tick();
            }
        });
    }
}
