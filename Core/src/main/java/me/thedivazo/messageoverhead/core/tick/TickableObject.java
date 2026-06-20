package me.thedivazo.messageoverhead.core.tick;

public interface TickableObject {
    default void onTickEnd() {
    }
    void tick();
}
