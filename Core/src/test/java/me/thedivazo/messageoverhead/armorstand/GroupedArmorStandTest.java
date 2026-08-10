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
                    CapturingArmorStand stand = new CapturingArmorStand(component);
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
                (component, version) -> new CapturingArmorStand(component),
                0.25,
                new Position(1.0, 5.0, 2.0),
                MinecraftVersion.VERSION_1_19_3
        ));

        assertEquals(0, groupedArmorStand.countLines());
    }

    @Test
    void supportsLiveLineEditsAndKeepsTextSnapshotSynchronized() {
        List<CapturingArmorStand> stands = new ArrayList<>();
        GroupedArmorStand groupedArmorStand = groupedArmorStand(
                List.of(Component.text("one"), Component.text("two")),
                stands
        );

        Component updated = Component.text("updated");
        Component added = Component.text("three");

        groupedArmorStand.setLine(1, updated);
        groupedArmorStand.addLine(added);
        Component removed = groupedArmorStand.removeLine(0);

        assertEquals(Component.text("one"), removed);
        assertEquals(List.of(updated, added), groupedArmorStand.getLines());
        assertEquals(2, groupedArmorStand.countLines());
        assertEquals(updated, stands.get(1).text);
        assertEquals(added, stands.get(2).text);
    }

    @Test
    void replacesAllLinesByGrowingAndShrinkingStandList() {
        List<CapturingArmorStand> stands = new ArrayList<>();
        GroupedArmorStand groupedArmorStand = groupedArmorStand(
                List.of(Component.text("one")),
                stands
        );

        groupedArmorStand.setLines(List.of(
                Component.text("alpha"),
                Component.text("beta"),
                Component.text("gamma")
        ));

        assertEquals(3, groupedArmorStand.countLines());
        assertEquals(List.of(
                Component.text("alpha"),
                Component.text("beta"),
                Component.text("gamma")
        ), groupedArmorStand.getLines());
        assertEquals(3, stands.size());

        groupedArmorStand.update(null);
        groupedArmorStand.onEndPlayersUpdate();

        groupedArmorStand.setLines(List.of(Component.text("final")));
        groupedArmorStand.update(null);

        assertEquals(1, groupedArmorStand.countLines());
        assertEquals(List.of(Component.text("final")), groupedArmorStand.getLines());
        assertEquals(1, stands.get(1).hideCount);
        assertEquals(1, stands.get(2).hideCount);
    }

    @Test
    void queuesLiveEditActionsUntilEndOfPlayersUpdate() {
        List<CapturingArmorStand> stands = new ArrayList<>();
        GroupedArmorStand groupedArmorStand = groupedArmorStand(
                List.of(Component.text("one")),
                stands
        );

        groupedArmorStand.update(null);
        assertEquals(0, stands.get(0).showCount);
        assertEquals(0, stands.get(0).metadataUpdateCount);

        groupedArmorStand.setLine(0, Component.text("two"));
        groupedArmorStand.update(null);
        assertEquals(1, stands.get(0).metadataUpdateCount);

        groupedArmorStand.onEndPlayersUpdate();
        groupedArmorStand.update(null);
        assertEquals(1, stands.get(0).metadataUpdateCount);

        groupedArmorStand.addLine(Component.text("three"));
        CapturingArmorStand addedStand = stands.get(1);
        groupedArmorStand.update(null);
        assertEquals(1, addedStand.showCount);

        groupedArmorStand.onEndPlayersUpdate();
        groupedArmorStand.removeLine(1);
        groupedArmorStand.update(null);
        assertEquals(1, addedStand.hideCount);
    }

    private static GroupedArmorStand groupedArmorStand(
            List<Component> lines,
            List<CapturingArmorStand> stands
    ) {
        return new GroupedArmorStand(
                lines,
                (component, version) -> {
                    CapturingArmorStand stand = new CapturingArmorStand(component);
                    stands.add(stand);
                    return stand;
                },
                0.25,
                new Position(1.0, 5.0, 2.0),
                MinecraftVersion.VERSION_1_19_3
        );
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
        private Component text;
        private int showCount;
        private int hideCount;
        private int metadataUpdateCount;

        private CapturingArmorStand(Component text) {
            this.text = text;
        }

        @Override
        public void show(Player player) {
            showCount++;
        }

        @Override
        public void updatePosition(Player player) {
        }

        @Override
        public void updateMetadata(Player player) {
            metadataUpdateCount++;
        }

        @Override
        public void hide(Player player) {
            hideCount++;
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
        public void setText(Component text) {
            this.text = text;
        }
    }
}
