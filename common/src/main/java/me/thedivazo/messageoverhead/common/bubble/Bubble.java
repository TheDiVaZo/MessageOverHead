package me.thedivazo.messageoverhead.common.bubble;

import me.thedivazo.messageoverhead.common.message.Message;

import java.time.LocalDateTime;

/**
 * Интерфейс всплывающего сообщения.
 * @param <K> Класс, представляющий собой создателя, вызвавший всплывающее сообщение.
 * @param <T> Содержание сообщения. Может быть {@link String} или другой класс, представляющий текст
 *
 */
public interface Bubble<T extends Message<?>, K> {
    T message();
    K creator();
    LocalDateTime createdAt();
}
