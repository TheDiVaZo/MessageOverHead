package me.thedivazo.messageoverhead.core.tick;

import me.thedivazo.messageoverhead.core.ActiveBubble;

import java.util.Objects;

public final class SchedulableBubble {
    private final ActiveBubble bubble;
    private final TickableObject tickable;

    public SchedulableBubble(ActiveBubble bubble, TickableObject tickable) {
        this.bubble = Objects.requireNonNull(bubble, "bubble");
        this.tickable = Objects.requireNonNull(tickable, "tickable");
    }

    public ActiveBubble bubble() {
        return bubble;
    }

    TickableObject tickable() {
        return tickable;
    }
}
