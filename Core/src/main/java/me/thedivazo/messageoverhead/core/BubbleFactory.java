package me.thedivazo.messageoverhead.core;

import me.thedivazo.messageoverhead.util.Positionc;

public interface BubbleFactory {
    ActiveBubble createBubble(Message message, AuthorBubble author, Positionc positionc);
}
