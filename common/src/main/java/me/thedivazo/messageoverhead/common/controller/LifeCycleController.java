package me.thedivazo.messageoverhead.common.controller;

import me.thedivazo.messageoverhead.common.bubble.Bubble;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Consumer;

/**
 * @author TheDiVaZo
 * created on 10.10.2024
 */
public interface LifeCycleController<B extends Bubble<?, ?>> {
    /**
     * Запускает жизненный цикл всплывающего сообщения.
     *
     * @param bubble всплывающее сообщение для запуска
     */
    void start(@NonNull B bubble);

    /**
     * Приостанавливает жизненный цикл всплывающего сообщения.
     *
     * @param bubble всплывающее сообщение для приостановки
     */
    void pause(@NonNull B bubble);

    /**
     * Завершает жизненный цикл всплывающего сообщения.
     *
     * @param bubble всплывающее сообщение для завершения
     */
    void terminate(@NonNull B bubble);

    /**
     * Получает текущее состояние жизненного цикла всплывающего сообщения.
     *
     * @param bubble всплывающее сообщение для проверки
     * @return текущее состояние
     */
    @NonNull LifeCycleState getState(@NonNull B bubble);

    /**
     * Возможные состояния жизненного цикла.
     */
    enum LifeCycleState {
        RUNNING,
        PAUSED,
        TERMINATED
    }
}
