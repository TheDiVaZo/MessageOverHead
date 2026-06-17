package me.thedivazo.messageoverhead.core;

import me.thedivazo.messageoverhead.util.Positionc;

import java.util.UUID;

public interface AuthorBubble {
    UUID getUID();
    boolean isLive();
    Positionc getPosition();
    UUID getWorldUID();
}
