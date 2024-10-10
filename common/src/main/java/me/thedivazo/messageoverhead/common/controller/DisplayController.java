package me.thedivazo.messageoverhead.common.controller;

import me.thedivazo.messageoverhead.common.bubble.Bubble;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Set;
import java.util.function.Supplier;

/**
 * @author TheDiVaZo
 * created on 07.10.2024
 *
 * Предоставляет методы отображения сообщения и его контроля
 */
public interface DisplayController<B extends Bubble<?, ?>, S> {
    /**
     * Заново отображает отображаемое сообщение
     * @param bubble отображаемое сообщение
     * @return текущее отображаемое сообщение, или null, если такового нет.
     */
    @Nullable B reloadBubble(B bubble);

    /**
     * Метод, отображающий новое всплывающие сообщение.
     * Заменяет предыдущее отображаемое сообщение и возвращает его
     * @param bubble новое сообщение
     * @return предыдущие наблюдатели. Null, если сообщение изначально не отображалось
     */
    @Nullable Set<S> showBubble(B bubble, Set<S> spectators);

    /**
     * Метод, добавляющий наблюдателей к отображаемому сообщению и отображает его новым наблюдателям
     * @param bubble отображаемое сообщение
     * @param newSpectators новые наблюдатели
     * @return true, если наблюдатели успешно добавлены.
     */
    boolean addSpectatorAndShow(B bubble, Set<S> newSpectators);
    default boolean addSpectatorAndShow(B bubble, S... newSpectators) {
        return addSpectatorAndShow(bubble, Set.of(newSpectators));
    }

    /**
     * Метод, удаляющий отображаемое сообщение
     * @param bubble отображаемое сообщение
     * @return сообщение, которое отображалось. Иначе null
     */
    @Nullable Set<S> hideBubble(B bubble);

    /**
     * Метод, удаляющий наблюдателей от отображаемого сообщения и скрывающий его удаленным наблюдателям
     * @param bubble отображаемое сообщение
     * @param spectators наблюдатели
     * @return true, если наблюдатели успешно удалены.
     */
    boolean removeSpectatorAndHide(B bubble, Set<S> spectators);
    default boolean removeSpectatorAndHide(B bubble, S... spectators) {
        return removeSpectatorAndHide(bubble, Set.of(spectators));
    }

    /**
     * Метод, удаляющий dct отображаемое сообщения и скрывающий его от наблюдателей
     */
    void hideAll();
}
