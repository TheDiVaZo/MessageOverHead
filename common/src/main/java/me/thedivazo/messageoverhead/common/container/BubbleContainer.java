package me.thedivazo.messageoverhead.common.container;

import me.thedivazo.messageoverhead.common.bubble.Bubble;

import java.util.Map;


/**
 * The interface Bubble container.
 *
 * @param <K> key type
 */
public interface BubbleContainer<K> {

    Bubble<?, ?> getBubble(K key);

    boolean hasBubble(K key);

    void clearBubbles();

    Bubble<?, ?> setBubble(K key, Bubble<?, ?> bubble);

    Bubble<?, ?> removeBubble(K key);

    Map<K, Bubble<?, ?>> getActiveBubbles();
}
