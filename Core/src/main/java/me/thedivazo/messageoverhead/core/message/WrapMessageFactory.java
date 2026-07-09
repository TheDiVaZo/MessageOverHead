package me.thedivazo.messageoverhead.core.message;

import me.thedivazo.messageoverhead.core.Author;
import me.thedivazo.messageoverhead.core.Message;
import me.thedivazo.messageoverhead.util.AdventureUtil;
import net.kyori.adventure.text.Component;

import java.util.function.Function;

public class WrapMessageFactory implements MessageFactory {
    private final Function<String, Component> deserializer;
    private final int maxLineSize;
    private final int maxWorldSize;

    public WrapMessageFactory(
            Function<String, Component> deserializer,
            int maxLineSize, int maxWorldSize) {
        this.deserializer = deserializer;
        this.maxLineSize = maxLineSize;
        this.maxWorldSize = maxWorldSize;
    }

    @Override
    public Message create(Author author, String message) {
        Component text = deserializer.apply(message);
        return new Message(text, AdventureUtil.wrapMessage(text, maxLineSize, maxWorldSize));
    }
}
