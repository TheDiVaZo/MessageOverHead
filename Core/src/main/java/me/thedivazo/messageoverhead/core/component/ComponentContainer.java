package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.annotation.MainThread;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@MainThread
public interface ComponentContainer {
    BubbleComponent attachUnchecked(ComponentKey<?> key, BubbleComponentFactory<?> factory);

    default <T extends BubbleComponent> T attach(ComponentKey<T> key, BubbleComponentFactory<T> factory) {
        return key.type().cast(attachUnchecked(key, factory));
    };

    void attachGroup(Map<ComponentKey<?>,? extends BubbleComponentFactory<?>> components);

    <T extends BubbleComponent> @Nullable T detach(ComponentKey<T> key);

    <T extends BubbleComponent> @Nullable T get(ComponentKey<T> key);

    boolean contains(ComponentKey<?> key);
}
