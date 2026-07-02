package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.annotation.MainThread;
import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.component.scope.BubbleScopeComponent;
import me.thedivazo.messageoverhead.core.component.scope.ComponentScoped;
import me.thedivazo.messageoverhead.core.component.scope.ScopedFactory;
import me.thedivazo.messageoverhead.core.render.capability.PositionCapability;
import me.thedivazo.messageoverhead.util.Position;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@MainThread
public class PositionComponent implements BubbleScopeComponent<Position> {
    private static final ComponentKey<PositionComponent> KEY = new ComponentKey<>(
            ComponentId.of("messageoverhead", "position"),
            PositionComponent.class,
            new ComponentMetadata(
                    Set.of(PositionCapability.class),
                    TypeComponent.BUBBLE
            )
    );

    private final ActiveBubble activeBubble;
    private final PositionCapability positionCapability;
    private final Map<String, ComponentScoped<Position>> components = new LinkedHashMap<>();

    private final Position cachedPosition = new Position();

    private PositionComponent(ActiveBubble activeBubble, PositionCapability positionCapability) {
        this.activeBubble = activeBubble;
        this.positionCapability = positionCapability;
    }

    @Override
    public @Nullable ComponentScoped<Position> attach(String id, ScopedFactory<Position> scopedFactory) {
        Objects.requireNonNull(scopedFactory, "scopedFactory");
        String scopedId = requireScopedId(id);
        try {
            return addScoped(scopedId, createScoped(scopedId, scopedFactory));
        } catch (Exception | Error exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public @Nullable ComponentScoped<Position> detach(String id) {
        ComponentScoped<Position> detached = components.remove(requireScopedId(id));
        if (detached != null) {
            detached.onDetached();
        }
        return detached;
    }

    @Override
    public @Nullable ComponentScoped<Position> get(String id) {
        return components.get(requireScopedId(id));
    }

    @Override
    public boolean contains(String id) {
        return components.containsKey(requireScopedId(id));
    }

    private ComponentScoped<Position> addScoped(String id, ComponentScoped<Position> componentScoped) {
        String scopedId = requireScopedId(id);
        Objects.requireNonNull(componentScoped, "componentScoped");
        ComponentScoped<Position> previous = components.get(scopedId);
        if (previous == componentScoped) {
            return componentScoped;
        }
        componentScoped.onAttached(activeBubble);
        components.put(scopedId, componentScoped);
        if (previous != null) {
            previous.onDetached();
        }
        return componentScoped;
    }

    private ComponentScoped<Position> createScoped(String id, ScopedFactory<Position> scopedFactory) throws Exception {
        ComponentScoped<Position> componentScoped = scopedFactory.create(activeBubble);
        if (componentScoped == null) {
            throw new IllegalStateException("Scoped factory returned null for " + id);
        }
        return componentScoped;
    }

    private static String requireScopedId(String id) {
        return Objects.requireNonNull(id, "id");
    }

    @Override
    public void onTick() {
        cachedPosition.zero();
        double offsetX=0, offsetY=0, offsetZ=0;
        for (ComponentScoped<Position> component : components.values()) {
            component.onTick(cachedPosition);
            offsetX+= cachedPosition.x;
            offsetY+= cachedPosition.y;
            offsetZ+= cachedPosition.z;
            cachedPosition.zero();
        }

        positionCapability.setPosition(
                activeBubble.author().getPosition().x() + offsetX,
                activeBubble.author().getPosition().y() + offsetY,
                activeBubble.author().getPosition().z() + offsetZ
        );
    }

    @Override
    public void onDetached() {
        detachAllScoped();
    }

    private void detachAllScoped() {
        for (ComponentScoped<Position> component : components.values()) {
            try {
                component.onDetached();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        components.clear();
    }

    public static ComponentKey<PositionComponent> key() {
        return KEY;
    }

    public static PositionComponent attach(ActiveBubble activeBubble, Factory factory) {
        return activeBubble.container().attach(key(), factory);
    }

    public static Factory factory(Map<String, ScopedFactory<Position>> scopedFactories) {
        return new Factory(scopedFactories);
    }
    public static @Nullable PositionComponent detach(ActiveBubble activeBubble) {
        return activeBubble.container().detach(key());
    }

    public static @Nullable PositionComponent get(ActiveBubble activeBubble) {
        return activeBubble.container().get(key());
    }

    public static PositionComponent getOrAttach(ActiveBubble activeBubble) {
        if (activeBubble.container().contains(key())) {
            return get(activeBubble);
        }
        else return attach(activeBubble, Factory.EMPTY);
    }

    public static boolean contains(ActiveBubble activeBubble) {
        return activeBubble.container().contains(key());
    }

    public static final class Factory implements BubbleComponentFactory<PositionComponent> {
        public static final Factory EMPTY = new Factory(Map.of());

        private final Map<String, ScopedFactory<Position>> scopedFactories;

        public Factory(Map<String, ScopedFactory<Position>> scopedFactories) {
            Objects.requireNonNull(scopedFactories, "scopedFactories");
            Map<String, ScopedFactory<Position>> scopedFactoriesCopy = new LinkedHashMap<>();
            for (Map.Entry<String, ScopedFactory<Position>> entry : scopedFactories.entrySet()) {
                scopedFactoriesCopy.put(
                        requireScopedId(entry.getKey()),
                        Objects.requireNonNull(entry.getValue(), "scopedFactory")
                );
            }
            this.scopedFactories = Collections.unmodifiableMap(scopedFactoriesCopy);
        }

        @Override
        public PositionComponent create(ActiveBubble bubble) throws Exception {
            PositionComponent component = new PositionComponent(bubble, bubble.capabilities().requireCapability(PositionCapability.class));
            for (Map.Entry<String, ScopedFactory<Position>> entry : scopedFactories.entrySet()) {
                component.addScoped(entry.getKey(), component.createScoped(entry.getKey(), entry.getValue()));
            }
            return component;
        }
    }
}
