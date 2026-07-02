package me.thedivazo.messageoverhead.animation;

import me.thedivazo.messageoverhead.util.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnimationOffsetComponentScopedTest {
    @Test
    void reachesTargetOffsetInConfiguredTicks() {
        AnimationOffsetComponentScoped scoped = new AnimationOffsetComponentScoped(4, 8.0, 4.0, -2.0);
        Position position = new Position();

        scoped.onTick(position);
        assertPosition(position, 2.0, 1.0, -0.5);

        position.zero();
        scoped.onTick(position);
        assertPosition(position, 4.0, 2.0, -1.0);

        position.zero();
        scoped.onTick(position);
        assertPosition(position, 6.0, 3.0, -1.5);

        position.zero();
        scoped.onTick(position);
        assertPosition(position, 8.0, 4.0, -2.0);

        position.zero();
        scoped.onTick(position);
        assertPosition(position, 8.0, 4.0, -2.0);
    }

    @Test
    void appliesCubicBezierSpeedCurve() {
        AnimationOffsetComponentScoped scoped = new AnimationOffsetComponentScoped(
                4,
                8.0,
                0.0,
                0.0,
                0.42,
                0.0,
                1.0,
                1.0
        );
        Position position = new Position();

        scoped.onTick(position);
        assertTrue(position.x() < 2.0);

        position.zero();
        scoped.onTick(position);
        assertTrue(position.x() < 4.0);

        position.zero();
        scoped.onTick(position);
        assertTrue(position.x() < 6.0);

        position.zero();
        scoped.onTick(position);
        assertPosition(position, 8.0, 0.0, 0.0);
    }

    @Test
    void animatesFromCurrentOffsetWhenTargetOffsetChanges() {
        AnimationOffsetComponentScoped scoped = new AnimationOffsetComponentScoped(4, 8.0, 0.0, 0.0);
        Position position = new Position();

        scoped.onTick(position);
        assertPosition(position, 2.0, 0.0, 0.0);

        position.zero();
        scoped.onTick(position);
        assertPosition(position, 4.0, 0.0, 0.0);

        scoped.setTargetOffset(12.0, 6.0, -3.0);

        position.zero();
        scoped.onTick(position);
        assertPosition(position, 6.0, 1.5, -0.75);

        position.zero();
        scoped.onTick(position);
        assertPosition(position, 8.0, 3.0, -1.5);

        position.zero();
        scoped.onTick(position);
        assertPosition(position, 10.0, 4.5, -2.25);

        position.zero();
        scoped.onTick(position);
        assertPosition(position, 12.0, 6.0, -3.0);
    }

    @Test
    void rejectsNonPositiveTickDuration() {
        assertThrows(IllegalArgumentException.class, () -> new AnimationOffsetComponentScoped(0, 1.0, 1.0, 1.0));
    }

    @Test
    void rejectsInvalidOffsetValues() {
        AnimationOffsetComponentScoped scoped = new AnimationOffsetComponentScoped(4, 1.0, 1.0, 1.0);

        assertThrows(IllegalArgumentException.class, () -> scoped.setTargetOffset(Double.NaN, 1.0, 1.0));
    }

    @Test
    void rejectsInvalidCubicBezierTimeControlPoints() {
        assertThrows(IllegalArgumentException.class, () -> new AnimationOffsetComponentScoped.CubicBezier(
                -0.1,
                0.0,
                1.0,
                1.0
        ));
    }

    private static void assertPosition(Position position, double x, double y, double z) {
        assertEquals(x, position.x(), 0.0000001);
        assertEquals(y, position.y(), 0.0000001);
        assertEquals(z, position.z(), 0.0000001);
    }
}
