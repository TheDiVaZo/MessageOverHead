package me.thedivazo.messageoverhead.core.event;

import me.thedivazo.messageoverhead.core.ActiveBubble;

public record SpawnBubbleEvent(ActiveBubble bubble) implements BubbleEvent {
}
