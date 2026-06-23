package me.thedivazo.messageoverhead.core.component;

import org.jetbrains.annotations.Nullable;

public interface ComponentContainer {
    BubbleComponent attachUnchecked(ComponentKey<?> key, BubbleComponentFactory<?> factory);

    default <T extends BubbleComponent> T attach(ComponentKey<T> key, BubbleComponentFactory<T> factory) {
        return key.type().cast(attachUnchecked(key, factory));
    };

    <T extends BubbleComponent> @Nullable T detach(ComponentKey<T> key);

    <T extends BubbleComponent> @Nullable T get(ComponentKey<T> key);

    boolean contains(ComponentKey<?> key);
}
