package me.thedivazo.messageoverhead.common.message;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

/**
 * Interface representing a message object
 * @param <T> Message type. Can be a {@link String} or other class representing the original message
 */
public interface Message<T> {
    @NonNull List<T> text();
    @NonNull List<String> toStringText();
}
