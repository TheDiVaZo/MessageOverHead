package me.thedivazo.messageoverhead.util;

import me.thedivazo.messageoverhead.util.Container;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapContainer<K, B> implements Container<K, B> {
    protected Map<K, B> container = new ConcurrentHashMap<>();

    @Override
    public B get(@NonNull K key) {
        return container.get(key);
    }

    @Override
    public boolean has(@NonNull K key) {
        return container.containsKey(key);
    }

    @Override
    public void clear() {
        container.clear();
    }

    @Override
    @Nullable
    public B set(@NonNull K key, @NonNull B bubble) {
        return container.put(key, bubble);
    }

    @Override
    @Nullable
    public B remove(@NonNull K key) {
        return container.remove(key);
    }

    @Override
    @NonNull
    public Map<K, B> getContainer() {
        return Collections.unmodifiableMap(container);
    }
}
