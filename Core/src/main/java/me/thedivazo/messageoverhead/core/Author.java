package me.thedivazo.messageoverhead.core;

import me.thedivazo.messageoverhead.annotation.MainThread;
import me.thedivazo.messageoverhead.util.Positionc;

import java.util.UUID;

@MainThread
public interface Author {
    UUID getUID();
    boolean isLive();
    Positionc getPosition();
    UUID getWorldUID();
}
