package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.annotation.MainThread;
import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.component.scope.BubbleScopeComponent;
import me.thedivazo.messageoverhead.core.component.scope.ComponentScoped;
import me.thedivazo.messageoverhead.core.render.capability.RendererPosition;
import me.thedivazo.messageoverhead.util.Position;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@MainThread
public class PositionComponent implements BubbleScopeComponent<Position> {
    private static final ComponentKey<PositionComponent> KEY = new ComponentKey<>(
            ComponentId.of("messageoverhead", "position"),
            PositionComponent.class,
            new ComponentMetadata(
                    Set.of(RendererPosition.class),
                    TypeComponent.BUBBLE
            )
    );

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
        return KEY;
    }

    public static PositionComponent attach(ActiveBubble activeBubble, Factory factory) {
        return activeBubble.container().attach(key(), factory);
    }

    public static Factory factory(List<Function<ActiveBubble, ComponentScoped<Position>>> scopedFactories) {
        return new Factory(scopedFactories);
    }

    public static Factory factory(ComponentScoped<Position>... scopedComponents) {
        return new Factory(Arrays.stream(scopedComponents)
                .map(component -> (Function<ActiveBubble, ComponentScoped<Position>>) active -> component)
                .toList()
        );
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

    public static final class Factory implements BubbleComponentFactory<PositionComponent> {
        private final List<Function<ActiveBubble, ComponentScoped<Position>>> scopedFactories;

        public Factory(List<Function<ActiveBubble, ComponentScoped<Position>>> scopedFactories) {
            this.scopedFactories = List.copyOf(scopedFactories);
        }

        @Override
        public PositionComponent create(ComponentContext context) {
            PositionComponent component = new PositionComponent(context.bubble(), context.capabilityContainer().requireCapability(RendererPosition.class));
            scopedFactories.forEach(scopedFactory -> component.addScoped(scopedFactory.apply(context.bubble())));
            return component;
        }
    }
}
