package me.thedivazo.messageoverhead;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import me.thedivazo.messageoverhead.core.component.*;
import me.thedivazo.messageoverhead.core.render.capability.RendererPosition;
import me.thedivazo.messageoverhead.core.render.capability.RendererView;

import java.util.Set;

public final class ComponentService {
    private static final String PLUGIN_NAMESPACE = "messageoverhead";

    private final ComponentRegistry registry;

    public final ComponentKey<ViewComponent> VIEW;
    public final ComponentKey<PositionComponent> POSITION;
    public final ComponentKey<LifetimeComponent> LIFETIME;

    private final SetMultimap<String, ComponentKey<?>> otherNamespaceToKeys = MultimapBuilder.hashKeys().hashSetValues().build();

    public ComponentService(ComponentRegistry registry) {
        this.registry = registry;
        VIEW = registerPluginComponent("view", ViewComponent.class, new ComponentMetadata(Set.of(RendererView.class)));
        POSITION = registerPluginComponent("position", PositionComponent.class, new ComponentMetadata(Set.of(RendererPosition.class)));
        LIFETIME = registerPluginComponent("lifetime", LifetimeComponent.class, ComponentMetadata.EMPTY);
    }

    <T extends BubbleComponent> ComponentKey<T> registerPluginComponent(String value, Class<T> componentClass, ComponentMetadata metadata) {
        return registry.register(
                new ComponentId(PLUGIN_NAMESPACE, value),
                componentClass,
                metadata
        );
    }

    public synchronized <T extends BubbleComponent> ComponentKey<T> register(String namespace, String value, Class<T> componentClass, ComponentMetadata metadata) {
        if (namespace.equals(PLUGIN_NAMESPACE)) throw new IllegalArgumentException("Invalid namespace name (" + namespace + "). Please, rename namespace");

        ComponentKey<T> key = registry.register(
                new ComponentId(namespace, value),
                componentClass,
                metadata
        );
        otherNamespaceToKeys.put(namespace, key);

        return key;
    }

    public synchronized <T extends BubbleComponent> void unregister(String namespace) {
        if (namespace.equals(PLUGIN_NAMESPACE)) throw new IllegalArgumentException("Invalid namespace name (" + namespace + "). Please, rename namespace");

        otherNamespaceToKeys.removeAll(namespace).forEach(registry::unregister);
    }

}
