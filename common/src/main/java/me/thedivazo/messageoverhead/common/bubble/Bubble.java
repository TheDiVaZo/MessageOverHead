package me.thedivazo.messageoverhead.common.bubble;

import me.thedivazo.messageoverhead.common.message.Message;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Interface representing a toast message object
 */
public interface Bubble<T extends Message> {
    T message();
    UUID creatorUUID();
    LocalDateTime createdAt();
    void show();
    void hide();
    void remove();
}
