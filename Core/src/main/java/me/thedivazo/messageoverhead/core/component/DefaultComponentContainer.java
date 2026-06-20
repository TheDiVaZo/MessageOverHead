package me.thedivazo.messageoverhead.core.component;

import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class DefaultComponentContainer implements ComponentContainer {
    private final ComponentRegistry registry;
    private final ComponentContext context;

    private final Map<ComponentKey<?>, Entry<?>> components =
            new LinkedHashMap<>();

    public DefaultComponentContainer(ComponentRegistry registry, ComponentContext context) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.context = Objects.requireNonNull(context, "context");
    }

    @Override
    public <T extends BubbleComponent> @Nullable T attach(
            ComponentKey<T> key,
            BubbleComponentFactory<T> factory
    ) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(factory, "factory");

        if (!registry.isValid(key)) return null;

        if (!factory.isAttachable(context)) {
            return null;
        }

        T component = createComponent(key, factory);
        Entry<T> entry = new Entry<>(key, component);
        Entry<?> previousEntry = components.remove(key);

        if (previousEntry != null) {
            previousEntry.component().onDetached();
        }

        components.put(key, entry);

        try {
            component.onAttached();
        } catch (RuntimeException | Error exception) {
            components.remove(key);
            throw exception;
        }

        return component;
    }

    @Override
    public <T extends BubbleComponent> @Nullable T detach(ComponentKey<T> key) {
        Objects.requireNonNull(key, "key");

        Entry<T> entry = (Entry<T>) components.get(key);

        if (entry == null) {
            return null;
        }

        components.remove(key);

        entry.component().onDetached();

        return entry.component();
    }

    @Override
    public <T extends BubbleComponent> @Nullable T get(ComponentKey<?> key) {
        Objects.requireNonNull(key, "key");

        if (!registry.isValid(key)) return null;

        Entry<T> entry = (Entry<T>) components.get(key);

        if (entry == null) {
            return null;
        }

        return entry.component();
    }

    @Override
    public boolean contains(ComponentKey<?> key) {
        Objects.requireNonNull(key, "key");

        if (!registry.isValid(key)) return false;

        return components.containsKey(key);
    }

    public void tick() {
        components.values().forEach(entry -> {
            entry.component().onTick();
        });
    }

    public void detachAll() {
        List<ComponentKey<?>> keys = new ArrayList<>();

        for (Entry<?> entry : components.values()) {
            keys.add(entry.key());
        }

        for (ComponentKey<?> key : keys) {
            detach(key);
        }
    }

    public int size() {
        return components.size();
    }

    public boolean isEmpty() {
        return components.isEmpty();
    }

    private <T extends BubbleComponent> T createComponent(
            ComponentKey<T> key,
            BubbleComponentFactory<T> factory
    ) {
        try {
            T component = factory.create(context);

            if (component == null) {
                throw new IllegalStateException(
                        "Component factory returned null for " + key.id()
                );
            }

            if (!key.type().isInstance(component)) {
                throw new IllegalStateException(
                        "Component factory for " + key.id()
                                + " returned "
                                + component.getClass().getName()
                                + ", expected "
                                + key.type().getName()
                );
            }

            return component;
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to create component " + key.id(),
                    exception
            );
        }
    }

    private record Entry<T extends BubbleComponent>(
            ComponentKey<T> key,
            T component
    ) {
    }
}
