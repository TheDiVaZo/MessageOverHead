package me.thedivazo.messageoverhead.core;

import me.thedivazo.messageoverhead.core.tick.TickableObject;

public interface TickableActiveBubble extends TickableObject {
    ActiveBubble bubble();
}
