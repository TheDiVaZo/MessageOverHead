package me.thedivazo.messageoverhead.core.render;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface RendererBubble {
    void destroy();
    boolean isDestroyed();

    <T> @Nullable T capabilityOrNull(Class<T> type);
    default <T> Optional<T> capability(Class<T> type) {
        return Optional.ofNullable(capabilityOrNull(type));
    }
}