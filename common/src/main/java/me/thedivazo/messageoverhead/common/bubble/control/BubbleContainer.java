package me.thedivazo.messageoverhead.common.bubble.control;

import me.thedivazo.messageoverhead.common.bubble.Bubble;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BubbleContainer {
    protected Map<UUID, Bubble<?>> activeBubbles = new ConcurrentHashMap<>();

    public Bubble<?> addBubble(Bubble<?> bubble) {
        return activeBubbles.put(bubble.creatorUUID(), bubble);
    }

    public Bubble<?> removeBubble(UUID creator) {
        return activeBubbles.remove(creator);
    }

    public Map<UUID, Bubble<?>> getActiveBubbles() {
        return Collections.unmodifiableMap(activeBubbles);
    }
}
