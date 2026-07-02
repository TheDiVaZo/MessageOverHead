package me.thedivazo.messageoverhead.core.component.scope;

import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.component.PositionComponent;
import me.thedivazo.messageoverhead.util.Position;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class OffsetComponentScoped implements ComponentScoped<Position> {
    public static final String DEFAULT_SCOPED_ID = "default-offset";

    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;

    public OffsetComponentScoped(double offsetX, double offsetY, double offsetZ) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    public static ScopedFactory<Position> factory(double offsetX, double offsetY, double offsetZ) {
        return ignored -> new OffsetComponentScoped(offsetX, offsetY, offsetZ);
    }

    public static @Nullable OffsetComponentScoped getOrAttach(
            ActiveBubble bubble,
            double offsetX,
            double offsetY,
            double offsetZ
    ) {
        Objects.requireNonNull(bubble, "bubble");
        return getOrAttach(PositionComponent.getOrAttach(bubble), offsetX, offsetY, offsetZ);
    }

    public static @Nullable OffsetComponentScoped getOrAttach(
            PositionComponent positionComponent,
            double offsetX,
            double offsetY,
            double offsetZ
    ) {
        Objects.requireNonNull(positionComponent, "positionComponent");

        ComponentScoped<Position> scoped = positionComponent.get(DEFAULT_SCOPED_ID);
        if (scoped instanceof OffsetComponentScoped offsetComponent) {
            return offsetComponent;
        }

        ComponentScoped<Position> attached = positionComponent.attach(
                DEFAULT_SCOPED_ID,
                factory(offsetX, offsetY, offsetZ)
        );
        if (attached instanceof OffsetComponentScoped offsetComponent) {
            return offsetComponent;
        }
        return null;
    }

    @Override
    public void onTick(Position context) {
        context.x += offsetX;
        context.y += offsetY;
        context.z += offsetZ;
    }
}
