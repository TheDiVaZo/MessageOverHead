package me.thedivazo.messageoverhead.common.bubble.factory;

import me.thedivazo.messageoverhead.common.bubble.Bubble;
import me.thedivazo.messageoverhead.common.message.Message;

import java.util.UUID;

public interface BubbleFactory<T extends Bubble<M>, M extends Message> {
    T createBubble(UUID creatorUUID, M message);
}
