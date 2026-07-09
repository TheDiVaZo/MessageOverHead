package me.thedivazo.messageoverhead.armorstand;

import me.thedivazo.messageoverhead.util.MinecraftVersion;
import me.thedivazo.messageoverhead.util.Position;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class GroupedArmorStandTest {
    @Test
    void createsStandsWithInitializedServerVersionAndAppliesLayout() {
        List<CapturingArmorStand> stands = new ArrayList<>();
        MinecraftVersion serverVersion = MinecraftVersion.VERSION_1_19_3;

        GroupedArmorStand groupedArmorStand = new GroupedArmorStand(
                List.of(Component.text("top"), Component.text("bottom")),
                (component, version) -> {
                    assertSame(serverVersion, version);
                    CapturingArmorStand stand = new CapturingArmorStand();
                    stands.add(stand);
                    return stand;
                },
                0.25,
                new Position(1.0, 5.0, 2.0),
                serverVersion
        );

        assertEquals(2, groupedArmorStand.countLines());
        assertPosition(stands.get(0), 1.0, 5.0, 2.0);
        assertPosition(stands.get(1), 1.0, 4.75, 2.0);

        groupedArmorStand.setPosition(3.0, 7.0, 4.0);

        assertPosition(stands.get(0), 3.0, 7.0, 4.0);
        assertPosition(stands.get(1), 3.0, 6.75, 4.0);
    }

    @Test
    void acceptsEmptyLineList() {
        GroupedArmorStand groupedArmorStand = assertDoesNotThrow(() -> new GroupedArmorStand(
                List.of(),
                (component, version) -> new CapturingArmorStand(),
                0.25,
                new Position(1.0, 5.0, 2.0),
                MinecraftVersion.VERSION_1_19_3
        ));

        assertEquals(0, groupedArmorStand.countLines());
    }

    private static void assertPosition(CapturingArmorStand stand, double x, double y, double z) {
        assertEquals(x, stand.x, 0.0000001);
        assertEquals(y, stand.y, 0.0000001);
        assertEquals(z, stand.z, 0.0000001);
    }

    private static final class CapturingArmorStand implements ArmorStand {
        private double x;
        private double y;
        private double z;

        @Override
        public void show(Player player) {
        }

        @Override
        public void updatePosition(Player player) {
        }

        @Override
        public void updateMetadata(Player player) {
        }

        @Override
        public void hide(Player player) {
        }

        @Override
        public void destroy() {
        }

        @Override
        public void setPosition(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public void setText(Component v) {
        }
    }
}
