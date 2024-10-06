package me.thedivazo.messageoverhead.util;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;


/**
 * Это обычный контейнер объектов.
 *
 * @param <K> ключ
 * @param <V> значение
 */
public interface Container<K, V> {

    @Nullable
    V get(@NonNull K key);

    @NonNull
    default V getOrDefault(@NonNull K key, @NonNull V defaultValue) {
        V value = get(key);
        return value!=null? value : defaultValue;
    }

    boolean has(@NonNull K key);

    void clear();

    /**
     * @return Возвращает предыдущее значение
     */
    @Nullable
    V set(@NonNull K key, @NonNull V bubble);

    @Nullable
    V remove(@NonNull K key);

    @NonNull
    Map<K, V> getContainer();
}
