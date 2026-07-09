package me.thedivazo.messageoverhead.armorstand;

import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import me.thedivazo.messageoverhead.util.MinecraftVersion;
import me.thedivazo.messageoverhead.util.Position;
import me.thedivazo.messageoverhead.util.Positionc;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GroupedArmorStand {
    private final MinecraftVersion serverVersion;
    private final ArmorStand.Factory factory;
    private final LineLayout layout;

    private final Int2ObjectSortedMap<ArmorStand> actualStands = new Int2ObjectAVLTreeMap<>(Integer::compareTo);
    private final Set<ArmorStandAction> actionInNextUpdate = new ObjectLinkedOpenHashSet<>();

    private final Position position;
    private boolean destroyed;

    public GroupedArmorStand(
            List<Component> text,
            ArmorStand.Factory factory,
            double lineSpacing,
            Positionc position,
            MinecraftVersion serverVersion)
    {
        this.factory = Objects.requireNonNull(factory, "factory");
        this.layout = new LineLayout(lineSpacing);
        this.position = new Position(Objects.requireNonNull(position, "position"));
        this.serverVersion = Objects.requireNonNull(serverVersion, "serverVersion");

        setLines(Objects.requireNonNull(text, "text"));
    }

    public int countLines() {
        return actualStands.size();
    }

    public double getLineSpacing() {
        return layout.lineSpacing();
    }

    public void setPosition(double x, double y, double z) {
        if (destroyed) {
            return;
        }

        position.set(x, y, z);

        recalculatePosition();
    }

    public void show(Player player) {
        Objects.requireNonNull(player, "player");
        if (destroyed) {
            return;
        }
        actualStands.values().forEach(stand -> stand.show(player));
    }

    public void hide(Player player) {
        Objects.requireNonNull(player, "player");
        if (destroyed) {
            return;
        }
        actualStands.values().forEach(stand -> stand.hide(player));
    }

    public void updatePosition(Player player) {
        actualStands.values().forEach(stand -> stand.updatePosition(player));
    }

    public void setText(List<Component> lines) {
        int size = lines.size();
        Iterator<Int2ObjectMap.Entry<ArmorStand>> iterator = actualStands.int2ObjectEntrySet().iterator();
        while (iterator.hasNext()) {
            Int2ObjectMap.Entry<ArmorStand> entry = iterator.next();
            if (entry.getIntKey() >= size) {
                actionInNextUpdate.add(ArmorStandAction.remove(entry.getValue()));
                iterator.remove();
            }
        }
        setLines(lines);
    }
    private void setLines(List<Component> lines) {
        for (int i = 0; i < lines.size(); i++) {
            setLine(i, lines.get(i));
        }
        recalculatePosition();
    }
    public void setText(int number, @Nullable Component line) {
        setLine(number, line);
        recalculatePosition();
    }
    private void setLine(int number, @Nullable Component line) {
        if (number < 0) throw new IllegalArgumentException("number < 0");

        ArmorStand prevArmorStand = actualStands.get(number);
        if (prevArmorStand != null && line == null) {
            actionInNextUpdate.add(ArmorStandAction.remove(prevArmorStand));
        }
        else if (prevArmorStand != null) {
            prevArmorStand.setText(line);
            actionInNextUpdate.add(ArmorStandAction.update(prevArmorStand));
        }
        else if (line != null) {
            ArmorStand stand = factory.create(line, serverVersion);
            actualStands.put(number, stand);
            actionInNextUpdate.add(ArmorStandAction.add(stand));
        }
    }

    public void update(Player player) {
        updatePosition(player);
        actionInNextUpdate.forEach(actionStand -> actionStand.execute(player));
    }

    public void endPlayersUpdate() {
        actionInNextUpdate.clear();
    }

    public void destroy() {
        if (destroyed) {
            return;
        }

        destroyed = true;
        actualStands.values().forEach(ArmorStand::destroy);
    }

    private void recalculatePosition() {
        if (actualStands.isEmpty()) {
            return;
        }

        int lastIndex = actualStands.lastIntKey();
        actualStands.int2ObjectEntrySet().forEach(entry -> {
            Position linePosition = layout.positionForLine(position, lastIndex, entry.getIntKey());
            entry.getValue().setPosition(linePosition.x, linePosition.y, linePosition.z);
        });
    }

    private record ArmorStandAction(Action action, ArmorStand stand) {
        private enum Action {
            ADD,
            REMOVE,
            UPDATE
        }

        public static ArmorStandAction remove(ArmorStand stand) {
            return new ArmorStandAction(Action.REMOVE, stand);
        }

        public static ArmorStandAction add(ArmorStand stand) {
            return new ArmorStandAction(Action.ADD, stand);
        }

        public static ArmorStandAction update(ArmorStand stand) {
            return new ArmorStandAction(Action.UPDATE, stand);
        }

        public void execute(Player player) {
            switch (action) {
                case ADD -> {
                    stand.show(player);
                }
                case REMOVE -> {
                    stand.hide(player);
                }
                case UPDATE -> {
                    stand.updateMetadata(player);
                }
            }
        }
    }

    record LineLayout(double lineSpacing) {

        Position positionForLine(Positionc basePosition, int lastLineIndex, int lineIndex) {
            Objects.requireNonNull(basePosition, "basePosition");
            if (lastLineIndex < 0) {
                throw new IllegalArgumentException("lastLineIndex < 0");
            }
            if (lineIndex < 0) {
                throw new IllegalArgumentException("lineIndex < 0");
            }

            return new Position(
                    basePosition.x(),
                    basePosition.y() + yOffset(lastLineIndex, lineIndex),
                    basePosition.z()
            );
        }

        private double yOffset(int lastLineIndex, int lineIndex) {
            return lineSpacing * (lastLineIndex - lineIndex - 1);
        }
    }
}
