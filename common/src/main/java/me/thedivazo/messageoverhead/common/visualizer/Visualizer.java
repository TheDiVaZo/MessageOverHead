package me.thedivazo.messageoverhead.common.visualizer;

import me.thedivazo.messageoverhead.common.bubble.Bubble;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;

/**
 * Класс, отвечающий за логику отображения всплывающего сообщения
 * @param <B> тип всплывающего сообщения
 * @param <S> тип наблюдателей
 */
public interface Visualizer<B extends Bubble<?, ?>, S> {
    void visualize(@NonNull B bubble, Set<S> spectators);
    void removeVisualization(@NonNull B bubble, Set<S> spectators);
}
