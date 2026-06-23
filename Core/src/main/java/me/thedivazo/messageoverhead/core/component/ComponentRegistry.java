package me.thedivazo.messageoverhead.core.component;

import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class ComponentRegistry {
    private final Map<ComponentId, ComponentKey<?>> keys =
            new LinkedHashMap<>();

    private boolean frozen;

    public <T extends BubbleComponent> void register(ComponentKey<T> componentKey) {
        Objects.requireNonNull(componentKey, "key");

        ComponentKey<?> existing = keys.get(componentKey.id());

        if (existing != null) {
            if (!existing.type().equals(componentKey.type())) {
                throw new IllegalStateException(
                        "Component ID " + componentKey.id()
                                + " is already registered for "
                                + existing.type().getName()
                                + ", cannot register "
                                + componentKey.type().getName()
                );
            }
        }

        if (frozen) {
            throw new IllegalStateException(
                    "Component registry is frozen"
            );
        }

        keys.put(componentKey.id(), componentKey);
    }

    public <T extends BubbleComponent> void unregister(
            ComponentKey<T> key
    ) {
        Objects.requireNonNull(key, "key");

        ComponentKey<?> existing = keys.get(key.id());

        if (existing != null) {
            if (!existing.type().equals(key.type())) {
                throw new IllegalStateException(
                        "Component ID " + key.id()
                                + " is already registered for "
                                + existing.type().getName()
                                + ", cannot unregister "
                                + key.type().getName()
                );
            }
        }

        if (frozen) {
            throw new IllegalStateException(
                    "Component registry is frozen"
            );
        }

        keys.remove(key.id());
    }

    public @Nullable ComponentKey<?> find(ComponentId id) {
        return keys.get(id);
    }

    public <T extends BubbleComponent> @Nullable ComponentKey<T> find(
            ComponentId id,
            Class<T> expectedType
    ) {
        ComponentKey<?> key = keys.get(id);

        if (key == null) {
            return null;
        }

        if (!key.type().equals(expectedType)) {
            throw new IllegalStateException(
                    "Component " + id + " has type "
                            + key.type().getName()
                            + ", expected "
                            + expectedType.getName()
            );
        }

        return castExisting(key, expectedType);
    }

    public boolean isValid(ComponentKey<?> key) {
        Objects.requireNonNull(key, "key");

        ComponentKey<?> existing = keys.get(key.id());

        if (existing == null) {
            return false;
        }

        if (!existing.type().equals(key.type())) {
            throw new IllegalStateException(
                    "Component " + key.id() + " has type "
                            + existing.type().getName()
                            + ", expected "
                            + key.type().getName()
            );
        }

        return key.equals(existing);
    }

    public void freeze() {
        frozen = true;
    }

    @SuppressWarnings("unchecked")
    private static <T extends BubbleComponent> ComponentKey<T> castExisting(
            ComponentKey<?> key,
            Class<T> type
    ) {
        if (!key.type().equals(type)) {
            throw new IllegalArgumentException("Incompatible key type");
        }

        return (ComponentKey<T>) key;
    }
}
