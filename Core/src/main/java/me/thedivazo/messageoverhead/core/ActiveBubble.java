package me.thedivazo.messageoverhead.core;

import me.thedivazo.messageoverhead.core.component.ComponentContainer;

import java.util.UUID;

public interface ActiveBubble {
    AuthorBubble author();

    UUID uuid();
    Message message();
    long ageTicks();
    boolean isRemove();
    void remove();

    ComponentContainer container();
}
