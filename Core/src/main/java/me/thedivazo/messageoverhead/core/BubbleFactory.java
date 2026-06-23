package me.thedivazo.messageoverhead.core;

import me.thedivazo.messageoverhead.util.Positionc;
import me.thedivazo.messageoverhead.core.tick.SchedulableBubble;

public interface BubbleFactory {
    SchedulableBubble createBubble(Message message, Author author, Positionc positionc);
}
