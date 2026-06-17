package me.thedivazo.messageoverhead.core.render.capability;

import me.thedivazo.messageoverhead.util.Positionc;

public interface RendererPosition extends RendererCapability {
    void setPosition(double x, double y, double z);
    Positionc getPosition();
}
