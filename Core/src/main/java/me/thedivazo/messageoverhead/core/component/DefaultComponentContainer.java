package me.thedivazo.messageoverhead.core.component;

import kotlin.collections.MapsKt;
import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.render.capability.CapabilityContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class DefaultComponentContainer implements ComponentContainer {
    private final ComponentRegistry registry;
    private final ActiveBubble context;

    private int counter = 0;

    private final Map<ComponentId, Entry<?>> components = new LinkedHashMap<>();
    private final SortedSet<Entry<?>> sortedComponents = new TreeSet<>(Comparator.naturalOrder());

    private boolean closed = false;

    private boolean isMutating = false;

    private final PrepareComponents cachedPrepareComponents = new PrepareComponents();

    public DefaultComponentContainer(ComponentRegistry registry, ActiveBubble context) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.context = Objects.requireNonNull(context, "context");
    }

    @Override
    public BubbleComponent attachUnchecked(ComponentKey<?> key, BubbleComponentFactory<?> factory) {
        if (isMutating || closed) return null;

        List<BubbleComponent> attachedComponents = null;
        isMutating = true;
        try {
            cachedPrepareComponents.invalidate();
            if (!cachedPrepareComponents.addPreparedEntry(key, factory)) {
                isMutating = false;
                return null;
            }

            attachedComponents = cachedPrepareComponents.applyAttachNewEntries();

            return attachedComponents == null || attachedComponents.isEmpty() ? null : attachedComponents.get(0);
        } finally {
            callPostInit(attachedComponents);
            isMutating = false;
        }
    }

    @Override
    public List<BubbleComponent> attachGroup(Map<ComponentKey<?>, ? extends BubbleComponentFactory<?>> components) {
        if (isMutating || closed) return null;

        isMutating = true;
        List<BubbleComponent> attachedComponents = null;
        try {
            cachedPrepareComponents.invalidate();
            if (!MapsKt.all(
                    components,
                    entry -> cachedPrepareComponents.addPreparedEntry(entry.getKey(), entry.getValue())
            )) {
                isMutating = false;
                return null;
            }
            attachedComponents = cachedPrepareComponents.applyAttachNewEntries();

            return attachedComponents;
        } finally {
            callPostInit(attachedComponents);
            isMutating = false;
        }
    }

    private void callPostInit(@Nullable List<? extends BubbleComponent> components) {
        if (components == null) return;
        for (int i = 0; i < components.size(); i++) {
            BubbleComponent component = components.get(i);
            try {
                component.onPostInit();
            } catch (Exception | Error exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public <T extends BubbleComponent> @Nullable T detach(ComponentKey<T> key) {
        if (isMutating || closed) return null;

        isMutating = true;
        try {
            cachedPrepareComponents.invalidate();
            if (!cachedPrepareComponents.removePreparedEntry(key)) {
                isMutating = false;
                return null;
            }
            List<BubbleComponent> detachedComponents = cachedPrepareComponents.applyDetachRemovedEntries();
            return detachedComponents == null || detachedComponents.isEmpty() ? null : key.type().cast(detachedComponents.get(0));
        } finally {
            isMutating = false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T extends BubbleComponent> T get(ComponentKey<T> key) {
        Objects.requireNonNull(key, "key");
        if (closed) return null;

        if (!registry.isValid(key)) return null;

        Entry<T> entry = (Entry<T>) components.get(key.id());

        if (entry == null) {
            return null;
        }
        else return key.type().cast(entry.component());
    }

    @Override
    public boolean contains(ComponentKey<?> key) {
        Objects.requireNonNull(key, "key");
        if (closed) return false;

        if (!registry.isValid(key)) return false;

        return components.containsKey(key.id());
    }

    public void tick() {
        if (closed) return;
        List<Entry<?>> list = new ArrayList<>(sortedComponents);
        for (int i = 0; i < list.size(); i++) {
            if (closed) return;
            Entry<?> entry = list.get(i);
            if (entry == null || components.get(entry.key.id()) != entry) continue;
            try {
                entry.component.onTick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void detachAllAndClose() {
        if (closed) return;
        closed = true;
        cachedPrepareComponents.invalidate();
        ArrayList<Entry<?>> entries = new ArrayList<>(sortedComponents);
        for (int i = 0; i < entries.size(); i++) {
            Entry<?> component = entries.get(i);
            try {
                component.component.onDetached();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        components.clear();
        sortedComponents.clear();
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
            int difference = key.metadata().typeComponent().getPriority() - o.key.metadata().typeComponent().getPriority();
            return difference == 0 ? Integer.compare(getCount(), o.getCount()) : difference;
        }
    }

    private boolean keyIsValid(ComponentKey<?> key) {
        return registry.isValid(key) && hasRequiredCapabilities(key);
    }

    private boolean hasRequiredCapabilities(ComponentKey<?> key) {
        CapabilityContainer container = context.capabilities();

        for (Class<?> capability : key.metadata().requiredCapabilities()) {
            if (!container.hasCapability(capability)) {
                return false;
            }
        }
        return true;
    }

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

    private final class PrepareComponents {
        private int localCounter = counter;
        private final List<Entry<?>> newEntries = new ArrayList<>();
        private final List<@Nullable Entry<?>> oldEntries = new ArrayList<>();

        private final List<Entry<?>> removedEntries = new ArrayList<>();

        public boolean addPreparedEntry(
                ComponentKey<?> key,
                BubbleComponentFactory<?> factory
        ) {
            Objects.requireNonNull(key, "key");
            Objects.requireNonNull(factory, "factory");

            if (!keyIsValid(key)) return false;

            BubbleComponent component;
            Entry<?> previousEntry;
            try {
                component = createComponent(key, factory);
                if (!key.type().isInstance(component)) throw new IllegalArgumentException(key + " is not of type " + component.getClass().getName() + ", key is type "+key.type().getName());
                previousEntry = components.get(key.id());

            } catch (Exception | Error exception) {
                exception.printStackTrace();
                return false;
            }

            Entry<?> entry = new Entry<>(key, component, localCounter++);
            newEntries.add(entry);
            oldEntries.add(previousEntry);
            return true;
        }

        public boolean removePreparedEntry(
                ComponentKey<?> key
        ) {
            Objects.requireNonNull(key, "key");

            Entry<?> entry = components.get(key.id());

            if (entry == null) {
                return false;
            }

            removedEntries.add(entry);
            return true;
        }

        public List<BubbleComponent> applyDetachRemovedEntries() {
            List<BubbleComponent> removedComponents = new ArrayList<>();
            for (int i = 0; i < removedEntries.size(); i++) {
                Entry<?> entry = removedEntries.get(i);
                try {
                    boolean isRemoved = components.remove(entry.key.id(), entry) | sortedComponents.remove(entry);
                    if (!isRemoved) continue;

                    entry.component.onDetached();
                    removedComponents.add(entry.component);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            counter = localCounter;
            return removedComponents;
        }

        public List<BubbleComponent> applyAttachNewEntries() {
            List<BubbleComponent> addedComponents = new ArrayList<>();
            for (int i = 0; i < newEntries.size(); i++) {
                Entry<?> oldEntry = oldEntries.get(i);
                Entry<?> entry = newEntries.get(i);

                try {
                    entry.component.onPreAttached();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (closed) return null;
                    continue;
                }
                if (closed) return null;


                if (oldEntry != null) {try {
                    components.remove(oldEntry.key.id(), oldEntry);
                    sortedComponents.remove(oldEntry);
                    oldEntry.component.onDetached();
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }}

                if (closed) return null;

                if (components.put(entry.key.id(), entry) != null | !sortedComponents.add(entry)) {
                    Throwable detachException = null;
                    try {
                        entry.component().onDetached();
                    }  catch (Throwable e) {
                        detachException = e;
                    }
                    detachAllAndClose();
                    IllegalStateException exception = new IllegalStateException("Failed to add a component " + entry.key.id());
                    if (detachException != null) {
                        exception.addSuppressed(detachException);
                    }
                    throw exception;
                }
                addedComponents.add(entry.component);
            }
            counter = localCounter;
            return addedComponents;
        }

        public void invalidate() {
            localCounter = counter;
            removedEntries.clear();
            newEntries.clear();
            oldEntries.clear();
        }
    }
}
