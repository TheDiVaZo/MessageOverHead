package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.core.tick.TickableObject;
import org.jetbrains.annotations.Nullable;

public interface ComponentProvider {
    <T> @Nullable T getComponent(Class<T> clazz);

    <T> boolean hasComponent(Class<T> type);

    <T extends TickableObject> void setComponent(Class<T> clazz, T component);
}
