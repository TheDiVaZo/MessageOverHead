package me.thedivazo.messageoverhead.core;

import me.thedivazo.messageoverhead.core.component.ComponentContainer;

import java.util.UUID;

public interface ActiveBubble {
    Author author();

    UUID id();
    Message message();
    long ageTicks();
    boolean isRemove();
    void remove();

    ComponentContainer container();
}
