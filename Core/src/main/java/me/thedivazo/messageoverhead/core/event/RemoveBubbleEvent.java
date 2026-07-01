package me.thedivazo.messageoverhead.core.event;

import me.thedivazo.messageoverhead.core.ActiveBubble;

public record RemoveBubbleEvent(ActiveBubble bubble) implements BubbleEvent {
}
