package me.thedivazo.messageoverhead.common.message;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

/**
 * @author TheDiVaZo
 * created on 06.10.2024
 */
public class MessageImpl<T> extends AbstractMessage<T> {
    @NonNull
    private final List<String> stringText;

    protected MessageImpl(List<T> text) {
        super(text);
        this.stringText = text.stream().map(Object::toString).toList();
    }

    @Override
    public @NonNull List<String> toStringText() {
        return stringText;
    }
}
