package me.thedivazo.messageoverhead.common.controller;

import me.thedivazo.messageoverhead.common.bubble.Bubble;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Класс, отвечает за управление отображением всплывающего сообщения
 * @param <B> тип всплывающего сообщения
 * @param <K> тип создателя всплывающего сообщения
 */
public interface DisplayController<B extends Bubble<?, K>, K> {
    /**
     * Отображает новое сообщение, если предыдущего сообщения нет или удалено
     * @param newBubble новое сообщение
     */
    boolean showBubbleIfNotShowed(B newBubble);

    /**
     * Отображает новое сообщение
     * @param newBubble новое сообщение
     */
    void showBubbleAnyway(B newBubble);

    /**
     * Отображает отображаемое сообщение новым вычисленным наблюдателям
     * @param creator
     */
    boolean updateSpectators(K creator);
    default boolean updateSpectators(B bubble) {
        updateSpectators(bubble.creator());
    }

    /**
     * Отображает новое сообщение старым наблюдателям.
     * @param newBubble новое сообщение
     * @return старое отображаемое сообщение или null, если такого сообщения не было
     */
    @Nullable B replaceBubble(B newBubble);

    /**
     * Удаляет сообщение, если оно было отображено
     * @param bubble
     */
    default boolean removeBubble(B bubble) {
        return removeBubble(bubble.creator());
    }

    /**
     * Удаляет текущее отображаемое сообщение у создателя.
     * @param creator
     */
    boolean removeBubble(K creator);
}
