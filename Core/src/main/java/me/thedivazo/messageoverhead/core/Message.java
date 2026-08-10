package me.thedivazo.messageoverhead.core;

import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Objects;

public record Message(List<Component> lines) {
    public Message {
        Objects.requireNonNull(lines, "lines");
        lines = List.copyOf(lines);
    }
}
