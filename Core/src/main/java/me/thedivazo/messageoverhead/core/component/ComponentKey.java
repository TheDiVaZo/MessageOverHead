package me.thedivazo.messageoverhead.core.component;

public record ComponentKey<T extends BubbleComponent>(
        ComponentId id,
        Class<T> type
) {
}
