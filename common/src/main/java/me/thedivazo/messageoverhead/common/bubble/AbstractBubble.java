package me.thedivazo.messageoverhead.common.bubble;

import lombok.experimental.SuperBuilder;
import me.thedivazo.messageoverhead.common.message.Message;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class AbstractBubble<T extends Message> implements Bubble<T> {
    protected final T message;
    protected final UUID creatorUUID;
    protected final LocalDateTime createdAt;

    protected AbstractBubble(T message, UUID creatorUUID, LocalDateTime createdAt) {
        this.message = message;
        this.creatorUUID = creatorUUID;
        this.createdAt = createdAt;
    }

    @Override
    public T message() {
        return message;
    }

    @Override
    public UUID creatorUUID() {
        return creatorUUID;
    }

    @Override
    public LocalDateTime createdAt() {
        return createdAt;
    }
}
