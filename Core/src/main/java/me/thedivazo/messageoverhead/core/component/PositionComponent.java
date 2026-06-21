package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.MessageOverHeadPlugin;
import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.component.scope.BubbleScopeComponent;
import me.thedivazo.messageoverhead.core.component.scope.ComponentScoped;
import me.thedivazo.messageoverhead.core.render.capability.RendererPosition;
import me.thedivazo.messageoverhead.util.Position;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class PositionComponent implements BubbleScopeComponent<Position> {
    private final ActiveBubble activeBubble;
    private final RendererPosition rendererPosition;
    private final List<ComponentScoped<Position>> components = new ArrayList<>();

    private final Position cachedPosition = new Position();

    private PositionComponent(ActiveBubble activeBubble, RendererPosition rendererPosition) {
        this.activeBubble = activeBubble;
        this.rendererPosition = rendererPosition;
    }

    @Override
    public void attach(Function<ActiveBubble, ComponentScoped<Position>> scopedFactory) {
        Objects.requireNonNull(scopedFactory, "scopedFactory");
        addScoped(Objects.requireNonNull(scopedFactory.apply(activeBubble), "scopedFactory result"));
    }

    private boolean addScoped(ComponentScoped<Position> componentScoped) {
        return components.add(Objects.requireNonNull(componentScoped, "componentScoped"));
    }

    @Override
    public void onTick() {
        cachedPosition.zero();
        double offsetX=0, offsetY=0, offsetZ=0;
        for (int i = 0; i < components.size(); i++) {
            ComponentScoped<Position> component = components.get(i);
            component.onTick(cachedPosition);
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

    public static ComponentKey<PositionComponent> key() {
        return MessageOverHeadPlugin.getInstance().getComponentService().POSITION;
    }

    public static @Nullable PositionComponent attach(ActiveBubble activeBubble) {
        return activeBubble.container().attach(key(), Factory.INSTANCE);
    }

    public static @Nullable PositionComponent detach(ActiveBubble activeBubble) {
        return activeBubble.container().detach(key());
    }

    public static @Nullable PositionComponent get(ActiveBubble activeBubble) {
        return activeBubble.container().get(key());
    }

    public static boolean contains(ActiveBubble activeBubble) {
        return activeBubble.container().contains(key());
    }

    public enum Factory implements BubbleComponentFactory<PositionComponent> {
        INSTANCE;

        @Override
        public PositionComponent create(ComponentContext context) {
            return new PositionComponent(context.bubble(), context.capabilityContainer().requireCapability(RendererPosition.class));
        }
    }
}
