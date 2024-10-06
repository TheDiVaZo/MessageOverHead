package me.thedivazo.messageoverhead.common.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

/**
 * @author TheDiVaZo
 * created on 06.10.2024
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractMessage<T> implements Message<T> {
    @NonNull
    private final List<T> text;

    @Override
    public @NonNull List<T> text() {
        return text;
    }
}
