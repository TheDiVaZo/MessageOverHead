package me.thedivazo.messageoverhead;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import me.thedivazo.messageoverhead.core.BubbleContainer;
import me.thedivazo.messageoverhead.core.component.*;

import java.util.Objects;
import java.util.Set;

public final class ComponentService {
    private final ComponentRegistry registry;
    private final BubbleContainer bubbleContainer;

    private final SetMultimap<String, ComponentKey<?>> otherNamespaceToKeys = MultimapBuilder.hashKeys().hashSetValues().build();

    public ComponentService(ComponentRegistry registry, BubbleContainer bubbleContainer) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.bubbleContainer = Objects.requireNonNull(bubbleContainer, "bubbleContainer");
        registerPluginComponent(ViewComponent.key());
        registerPluginComponent(PositionComponent.key());
        registerPluginComponent(LifetimeComponent.key());
    }

    <T extends BubbleComponent> void registerPluginComponent(ComponentKey<T> key) {
        if (!isPluginNamespace(key.id().namespace())) throw new IllegalArgumentException("Invalid namespace name (" + key.id().namespace() + "). Please, rename namespace to " + pluginNamespace());
        registry.register(key);
    }

    public synchronized <T extends BubbleComponent> void register(ComponentKey<T> key) {
        if (isPluginNamespace(key.id().namespace())) throw new IllegalArgumentException("Invalid namespace name (" + key.id().namespace() + "). Please, rename namespace");

        registry.register(key);
        otherNamespaceToKeys.put(key.id().namespace(), key);
    }

    public synchronized <T extends BubbleComponent> void unregister(String namespace) {
        if (isPluginNamespace(namespace)) throw new IllegalArgumentException("Invalid namespace name (" + namespace + "). Please, rename namespace");

        Set<ComponentKey<?>> keys = Set.copyOf(otherNamespaceToKeys.get(namespace));

        if (bubbleContainer != null) {
            bubbleContainer.getBubblesByBubbleId().values().forEach(bubble -> {
                for (ComponentKey<?> key : keys) {
                    bubble.container().detach(key);
                }
            });
        }

        otherNamespaceToKeys.removeAll(namespace);
        keys.forEach(registry::unregister);
    }

    private boolean isPluginNamespace(String namespace) {
        return pluginNamespace().equals(namespace);
    }

    private String pluginNamespace() {
        return PositionComponent.key().id().namespace();
    }

}
