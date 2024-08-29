package me.thedivazo.messageoverhead.common.bubble;

import me.thedivazo.messageoverhead.common.message.Message;

import java.time.LocalDateTime;

public abstract class AbstractBubble<T extends Message<?>, K> implements Bubble<T, K> {
    protected final T message;
    protected final K creator;
    protected final LocalDateTime createdAt;

    protected AbstractBubble(T message, K creator, LocalDateTime createdAt) {
        this.message = message;
        this.creator = creator;
        this.createdAt = createdAt;
    }

    @Override
    public T message() {
        return message;
    }

    @Override
    public K creator() {
        return creator;
    }

    @Override
    public LocalDateTime createdAt() {
        return createdAt;
    }
}
