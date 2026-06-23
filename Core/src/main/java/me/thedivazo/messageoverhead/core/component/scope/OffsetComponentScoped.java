package me.thedivazo.messageoverhead.core.component.scope;

import me.thedivazo.messageoverhead.util.Position;

public class OffsetComponentScoped implements ComponentScoped<Position> {
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;

    public OffsetComponentScoped(double offsetX, double offsetY, double offsetZ) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    @Override
    public void onTick(Position context) {
        context.x += offsetX;
        context.y += offsetY;
        context.z += offsetZ;
    }
}
