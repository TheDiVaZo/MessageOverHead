package me.thedivazo.messageoverhead.core.render.capability;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public interface CapabilityContainer {
    <T> @Nullable T capabilityOrNull(Class<T> type);

    default  <T> T requireCapability(Class<T> type) {
        return Objects.requireNonNull(capabilityOrNull(type));
    };

    default <T>Optional<T> capability(Class<T> type) {
        return Optional.ofNullable(capabilityOrNull(type));
    }
}
