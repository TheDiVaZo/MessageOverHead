package me.thedivazo.messageoverhead.api;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import me.thedivazo.messageoverhead.annotation.MainThread;
import me.thedivazo.messageoverhead.core.BubbleContainer;
import me.thedivazo.messageoverhead.core.component.*;

import java.util.Objects;
import java.util.Set;

@MainThread
public final class ComponentService {
    private static final String PLUGIN_NAMESPACE = "messageoverhead";

    private final ComponentRegistry registry;
    private final BubbleContainer bubbleContainer;

    private final SetMultimap<String, ComponentKey<?>> otherNamespaceToKeys = MultimapBuilder.hashKeys().hashSetValues().build();

    public ComponentService(ComponentRegistry registry, BubbleContainer bubbleContainer) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.bubbleContainer = Objects.requireNonNull(bubbleContainer, "bubbleContainer");
        registerPluginComponent(ViewComponent.key());
        registerPluginComponent(PositionComponent.key());
        registerPluginComponent(TextComponent.key());
        registerPluginComponent(LifetimeComponent.key());
        registerPluginComponent(ProfileComponent.key());
    }

    private <T extends BubbleComponent> void registerPluginComponent(ComponentKey<T> key) {
        if (!Objects.equals(key.id().namespace(), PLUGIN_NAMESPACE)) {
            throw new IllegalArgumentException(
                    "Invalid namespace name (" + key.id().namespace() + "). Please, rename namespace to \"messageoverhead\""
            );
        }

        registry.register(key);
    }

    public synchronized <T extends BubbleComponent> void register(ComponentKey<T> key) {
        Objects.requireNonNull(key, "key");

        if (Objects.equals(key.id().namespace(), PLUGIN_NAMESPACE)) {
            throw new IllegalArgumentException(
                    "Invalid namespace name (" + key.id().namespace() + "). Please, rename namespace"
            );
        }

        registry.register(key);
        otherNamespaceToKeys.put(key.id().namespace(), key);
    }

    public synchronized void unregister(String namespace) {
        Objects.requireNonNull(namespace, "namespace");

        if (Objects.equals(namespace, PLUGIN_NAMESPACE)) {
            throw new IllegalArgumentException(
                    "Invalid namespace name (" + namespace + "). Please, rename namespace"
            );
        }

        Set<ComponentKey<?>> keys = Set.copyOf(otherNamespaceToKeys.get(namespace));

        bubbleContainer.getBubblesByBubbleId().values().forEach(bubble -> {
            for (ComponentKey<?> key : keys) {
                bubble.container().detach(key);
            }
        });

        otherNamespaceToKeys.removeAll(namespace);
        keys.forEach(registry::unregister);
    }

}
