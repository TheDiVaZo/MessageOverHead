package me.thedivazo.messageoverhead.core;

import me.thedivazo.messageoverhead.core.component.ComponentContainer;

public interface ActiveBubble {
    AuthorBubble author();

    Message message();
    long ageTicks();
    boolean isRemove();
    void remove();

    ComponentContainer container();
}
