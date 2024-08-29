package me.thedivazo.messageoverhead.common.container;

import me.thedivazo.messageoverhead.common.bubble.Bubble;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BubbleContainerHashMap<K> implements BubbleContainer<K> {
    protected Map<K, Bubble<?, ?>> activeBubbles = new ConcurrentHashMap<>();

    @Override
    public Bubble<?, ?> getBubble(K key) {
        return activeBubbles.get(key);
    }

    @Override
    public boolean hasBubble(K key) {
        return activeBubbles.containsKey(key);
    }

    @Override
    public void clearBubbles() {
        activeBubbles.clear();
    }

    @Override
    public Bubble<?, ?> setBubble(K key, Bubble<?, ?> bubble) {
        return activeBubbles.put(key, bubble);
    }

    @Override
    public Bubble<?, ?> removeBubble(K key) {
        return activeBubbles.remove(key);
    }

    @Override
    public Map<K, Bubble<?, ?>> getActiveBubbles() {
        return Collections.unmodifiableMap(activeBubbles);
    }
}
