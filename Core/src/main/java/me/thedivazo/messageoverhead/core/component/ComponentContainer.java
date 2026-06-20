package me.thedivazo.messageoverhead.core.component;

import org.jetbrains.annotations.Nullable;

public interface ComponentContainer {
    <T extends BubbleComponent> @Nullable T attach(ComponentKey<T> key, BubbleComponentFactory<T> factory);

    <T extends BubbleComponent> @Nullable T detach(ComponentKey<T> key);

    <T extends BubbleComponent> @Nullable T get(ComponentKey<?> key);

    boolean contains(ComponentKey<?> key);
}
