package me.thedivazo.messageoverhead.common.controller;

import me.thedivazo.messageoverhead.common.bubble.Bubble;
import me.thedivazo.messageoverhead.common.message.Message;

/**
 * Отвечает за управлением отображения сообщения наблюдателям
 * @param <B> тип всплывающего сообщения
 * @param <K> тип создателя всплывающего сообщения
 */
public interface DisplayController<B extends Bubble<?, K>, K> {
    /**
     * Отображает новое сообщение
     * @param newBubble новое сообщение
     */
    void showBubble(B newBubble);

    /**
     * Удаляет сообщение, если оно было отображено
     * @param bubble
     */
    void removeBubble(B bubble);

    /**
     * Показывает текущее отображаемое сообщение у создателя, которое было скрыто
     * Если сообщения нет (или удалено), или оно не было скрыто, ничего не делает
     * @param creator тип создателя
     */
    void showCurrentBubble(K creator);

    /**
     * Скрывает текущее отображаемое сообщение у создателя.
     * Если сообщение уже скрыто, ничего не делает
     * @param creator
     */
    void hideCurrentBubble(K creator);

    /**
     * Удаляет текущее отображаемое сообщение у создателя.
     * @param creator
     */
    void removeCurrentBubble(K creator);

    /**
     * Возвращает текущее отображаемое сообщение у создателя
     * @param creator создатель сообщения
     * @return текущее отображаемое сообщение
     */
    B getCurrentBubble(K creator);
}
