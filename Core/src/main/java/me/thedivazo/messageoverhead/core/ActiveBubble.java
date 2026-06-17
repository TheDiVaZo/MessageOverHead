package me.thedivazo.messageoverhead.core;

import org.bukkit.entity.Player;

public interface ActiveBubble {
    AuthorBubble author();

    Message message();
    long ageTicks();
    boolean isRemove();
    void remove();
}
