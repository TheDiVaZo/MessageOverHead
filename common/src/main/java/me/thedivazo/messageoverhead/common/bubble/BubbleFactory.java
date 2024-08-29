package me.thedivazo.messageoverhead.common.bubble;

import me.thedivazo.messageoverhead.common.message.Message;

public interface BubbleFactory<T extends Bubble<M, K>, M extends Message<?>, K> {
    T createBubble(K creator, M message);
}
