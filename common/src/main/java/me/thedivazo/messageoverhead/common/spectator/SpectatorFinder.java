package me.thedivazo.messageoverhead.common.spectator;

import me.thedivazo.messageoverhead.common.bubble.Bubble;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;

/**
 * Класс, отвечающий за алгоритм поиска наблюдателей для всплывающего сообщения
 * @param <S> тип всплывающего сообщения
 * @param <B> тип наблюдателя
 */
public interface SpectatorFinder<S, B extends Bubble<?, ?>> {
    @NonNull Set<S> getSpectators(B bubble);
}
