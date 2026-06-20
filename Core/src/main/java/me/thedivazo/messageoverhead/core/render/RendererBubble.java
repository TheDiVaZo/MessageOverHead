package me.thedivazo.messageoverhead.core.render;

import me.thedivazo.messageoverhead.core.render.capability.CapabilityContainer;

public interface RendererBubble extends CapabilityContainer {
    void destroy();
    boolean isDestroyed();
}