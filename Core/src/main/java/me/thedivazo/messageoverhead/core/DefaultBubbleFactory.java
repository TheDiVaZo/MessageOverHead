package me.thedivazo.messageoverhead.core;

import me.thedivazo.messageoverhead.annotation.MainThread;
import me.thedivazo.messageoverhead.core.component.ComponentRegistry;
import me.thedivazo.messageoverhead.core.event.EventBus;
import me.thedivazo.messageoverhead.core.render.RendererFactory;
import me.thedivazo.messageoverhead.core.tick.SchedulableBubble;
import me.thedivazo.messageoverhead.core.tick.TickableObject;
import me.thedivazo.messageoverhead.util.Positionc;

@MainThread
public class DefaultBubbleFactory implements BubbleFactory {
    private final ComponentRegistry componentRegistry;
    private final RendererFactory rendererFactory;
    private final EventBus eventBus;

    public DefaultBubbleFactory(ComponentRegistry componentRegistry, RendererFactory rendererFactory, EventBus eventBus) {
        this.componentRegistry = componentRegistry;
        this.rendererFactory = rendererFactory;
        this.eventBus = eventBus;
    }

    @Override
    public SchedulableBubble createBubble(Message message, Author author, Positionc positionc) {
        ActiveBubbleController bubble = new ActiveBubbleController(
                message,
                author,
                rendererFactory.create(message, positionc),
                componentRegistry,
                eventBus
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
