package me.thedivazo.messageoverhead.core.tick;

import me.thedivazo.messageoverhead.annotation.MainThread;

@MainThread
public interface TickableObject {
    default void onTickEnd() {
    }
    void tick();
}
