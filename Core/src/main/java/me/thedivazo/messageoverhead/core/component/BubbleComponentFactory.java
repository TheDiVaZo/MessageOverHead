package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.core.ActiveBubble;

public interface BubbleComponentFactory<T extends BubbleComponent> {
    T create(ActiveBubble bubble) throws Exception;
}
