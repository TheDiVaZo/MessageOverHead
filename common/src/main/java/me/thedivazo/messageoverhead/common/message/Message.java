package me.thedivazo.messageoverhead.common.message;

import java.util.List;

/**
 * Interface representing a message object
 * @param <T> Message type. Can be a {@link String} or other class representing the original message
 */
public interface Message<T> {
    List<T> text();
    List<String> toStringText();
}
