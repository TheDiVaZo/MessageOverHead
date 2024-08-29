package me.thedivazo.messageoverhead.common.bubble;

import me.thedivazo.messageoverhead.common.message.Message;

import java.time.LocalDateTime;

/**
 * Interface representing a toast message object
 */
public interface Bubble<T extends Message<?>, K> {
    T message();
    K creator();
    LocalDateTime createdAt();
}
