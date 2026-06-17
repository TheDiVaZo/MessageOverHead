package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.render.capability.RendererPosition;
import me.thedivazo.messageoverhead.util.Position;

import java.util.ArrayList;
import java.util.List;

public class ScopeComponentPosition implements ScopeComponent<Position> {
    private final ActiveBubble activeBubble;
    private final RendererPosition rendererPosition;
    private List<ComponentScoped<Position>> components = new ArrayList<>();

    private final Position cachedPosition = new Position();

    public ScopeComponentPosition(ActiveBubble activeBubble, RendererPosition rendererPosition) {
        this.activeBubble = activeBubble;
        this.rendererPosition = rendererPosition;
    }

    @Override
    public boolean add(ComponentScoped<Position> componentScoped) {
        return components.add(componentScoped);
    }

    @Override
    public void tick() {
        cachedPosition.zero();
        double offsetX=0, offsetY=0, offsetZ=0;
        for (int i = 0; i < components.size(); i++) {
            ComponentScoped<Position> component = components.get(i);
            component.apply(cachedPosition, activeBubble);
            offsetX+= cachedPosition.x;
            offsetY+= cachedPosition.y;
            offsetZ+= cachedPosition.z;
            cachedPosition.zero();
        }

        rendererPosition.setPosition(
                activeBubble.author().getPosition().x() + offsetX,
                activeBubble.author().getPosition().y() + offsetY,
                activeBubble.author().getPosition().z() + offsetZ
        );
    }
}
