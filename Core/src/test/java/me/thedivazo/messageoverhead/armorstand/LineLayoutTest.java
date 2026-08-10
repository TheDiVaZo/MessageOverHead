package me.thedivazo.messageoverhead.armorstand;

import me.thedivazo.messageoverhead.util.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineLayoutTest {
    @Test
    void placesSingleLineAtBasePosition() {
        GroupedArmorStand.LineLayout layout = new GroupedArmorStand.LineLayout(0.25);
        Position position = layout.positionForLine(new Position(1.0, 5.0, 2.0), 0, 0);

        assertPosition(position, 1.0, 5.0, 2.0);
    }

    @Test
    void placesBottomLineAtBasePosition() {
        GroupedArmorStand.LineLayout layout = new GroupedArmorStand.LineLayout(0.25);
        Position position = layout.positionForLine(new Position(1.0, 5.0, 2.0), 1, 1);

        assertPosition(position, 1.0, 5.0, 2.0);
    }

    @Test
    void stacksHigherLinesAboveBasePosition() {
        GroupedArmorStand.LineLayout layout = new GroupedArmorStand.LineLayout(0.25);

        assertPosition(
                layout.positionForLine(new Position(1.0, 5.0, 2.0), 2, 0),
                1.0, 5.5, 2.0
        );
        assertPosition(
                layout.positionForLine(new Position(1.0, 5.0, 2.0), 2, 1),
                1.0, 5.25, 2.0
        );
        assertPosition(
                layout.positionForLine(new Position(1.0, 5.0, 2.0), 2, 2),
                1.0, 5.0, 2.0
        );
    }

    @Test
    void rejectsNegativeIndexes() {
        GroupedArmorStand.LineLayout layout = new GroupedArmorStand.LineLayout(0.25);

        assertThrows(IllegalArgumentException.class, () -> layout.positionForLine(new Position(), -1, 0));
        assertThrows(IllegalArgumentException.class, () -> layout.positionForLine(new Position(), 0, -1));
    }

    private static void assertPosition(Position position, double x, double y, double z) {
        assertEquals(x, position.x(), 0.0000001);
        assertEquals(y, position.y(), 0.0000001);
        assertEquals(z, position.z(), 0.0000001);
    }
}
