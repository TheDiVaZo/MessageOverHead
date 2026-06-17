package me.thedivazo.messageoverhead.core;

import net.kyori.adventure.text.Component;

import java.util.Objects;

public record Message(Component component) {
    public Message {
        Objects.requireNonNull(component);
    }
}
