package me.thedivazo.messageoverhead.core.tick;

public interface TickableObject {
    default void onTickStart() {
    }
    default void onTickEnd(StopReason stopReason) {
    }
    void tick();
}
