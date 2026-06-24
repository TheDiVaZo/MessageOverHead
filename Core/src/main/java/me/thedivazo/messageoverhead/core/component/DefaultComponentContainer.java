package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.core.render.capability.CapabilityContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class DefaultComponentContainer implements ComponentContainer {
    private final ComponentRegistry registry;
    private final ComponentContext context;

    private int counter = 0;

    private final Map<ComponentId, Entry<?>> components = new LinkedHashMap<>();
    private final SortedSet<Entry<?>> sortedComponents = new TreeSet<>(Comparator.naturalOrder());

    public DefaultComponentContainer(ComponentRegistry registry, ComponentContext context) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.context = Objects.requireNonNull(context, "context");
    }

    @Override
    public BubbleComponent attachUnchecked(
            ComponentKey<?> key,
            BubbleComponentFactory<?> factory
    ) {
        BubbleComponent component = attackComponent(key, factory);
        component.onPostInit();
        return component;
    }

    private BubbleComponent attackComponent(
                ComponentKey<?> key,
                BubbleComponentFactory<?> factory
    ) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(factory, "factory");

        ensureValidKey(key);
        ensureAttachAllowed(key, context);

        BubbleComponent component = createComponent(key, factory);
        if (!key.type().isInstance(component)) throw new IllegalArgumentException(key + " is not of type " + component.getClass().getName() + ", key is type "+key.type().getName());

        Entry<?> entry = new Entry<>(key, component, counter++);
        Entry<?> previousEntry = components.remove(key.id());

        if (previousEntry != null) {
            sortedComponents.remove(previousEntry);
            previousEntry.component().onDetached();
        }

        components.put(key.id(), entry);
        sortedComponents.add(entry);

        try {
            component.onAttached();
        } catch (RuntimeException | Error exception) {
            components.remove(key.id());
            sortedComponents.remove(entry);
            throw exception;
        }

        return key.type().cast(component);
    }

    @Override
    public void attachGroup(Map<ComponentKey<?>, ? extends BubbleComponentFactory<?>> components) {
        List<BubbleComponent> attached = new ArrayList<>();
        for (Map.Entry<ComponentKey<?>, ? extends BubbleComponentFactory<?>> entry : components.entrySet()) {
            ComponentKey<?> key = entry.getKey();
            BubbleComponentFactory<?> factory = entry.getValue();
            attached.add(attackComponent(key, factory));
        }
        attached.forEach(BubbleComponent::onPostInit);
    }

    @Override
    public <T extends BubbleComponent> @Nullable T detach(ComponentKey<T> key) {
        Objects.requireNonNull(key, "key");

        Entry<T> entry = (Entry<T>) components.get(key.id());

        if (entry == null) {
            return null;
        }

        components.remove(key.id());
        sortedComponents.remove(entry);

        entry.component().onDetached();

        return entry.component();
    }

    @Override
    public <T extends BubbleComponent> @Nullable T get(ComponentKey<T> key) {
        Objects.requireNonNull(key, "key");

        if (!registry.isValid(key)) return null;

        Entry<T> entry = (Entry<T>) components.get(key.id());

        if (entry == null) {
            return null;
        }

        return entry.component();
    }

    @Override
    public boolean contains(ComponentKey<?> key) {
        Objects.requireNonNull(key, "key");

        if (!registry.isValid(key)) return false;

        return components.containsKey(key.id());
    }

    public void tick() {
        sortedComponents.forEach(entry -> {
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

    @SuppressWarnings("unchecked")
    private <T extends BubbleComponent> T createComponent(
            ComponentKey<?> key,
            BubbleComponentFactory<?> factory
    ) {
        try {
            T component = (T) factory.create(context);

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

    private static final class Entry<T extends BubbleComponent> implements Comparable<Entry<?>> {
        private final ComponentKey<?> key;
        private final BubbleComponent component;

        int getCount() {
            return count;
        }

        private final int count;

        private Entry(
                ComponentKey<?> key,
                BubbleComponent component,
                int count
        ) {
            this.key = key;
            this.component = component;
            this.count = count;
        }

        public ComponentKey<T> key() {
            return (ComponentKey<T>) key;
        }

        public T component() {
            return (T) component;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Entry) obj;
            return Objects.equals(this.key, that.key) &&
                    Objects.equals(this.component, that.component);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, component);
        }

        @Override
        public String toString() {
            return "Entry[" +
                    "key=" + key + ", " +
                    "component=" + component + ']';
        }

        @Override
        public int compareTo(@NotNull DefaultComponentContainer.Entry<?> o) {
            int difference = component.getType().getPriority() - o.component.getType().getPriority();
            return difference == 0 ? Integer.compare(getCount(), o.getCount()) : difference;
        }
    }

    private void ensureValidKey(ComponentKey<?> key) {
        if (!registry.isValid(key)) {
            throw new IllegalArgumentException("Component key is not registered: " + key.id());
        }
    }

    private void ensureAttachAllowed(ComponentKey<?> key, ComponentContext context) {
        CapabilityContainer container = context.capabilityContainer();

        for (Class<?> capability : key.metadata().requiredCapabilities()) {
            if (!container.hasCapability(capability)) {
                throw new IllegalStateException(
                        "Component " + key.id() + " requires renderer capability " + capability.getName()
                );
            }
        }

        if (!key.metadata().bubblePredicate().test(context.bubble())) {
            throw new IllegalStateException(
                    "Component " + key.id() + " cannot be attached to bubble " + context.bubble().id()
            );
        }
    }
}
