package me.thedivazo.messageoverhead.core.render;

import me.thedivazo.messageoverhead.core.Message;
import me.thedivazo.messageoverhead.util.Positionc;

public interface RendererFactory {
    RendererBubble create(Message message, Positionc position);
}
