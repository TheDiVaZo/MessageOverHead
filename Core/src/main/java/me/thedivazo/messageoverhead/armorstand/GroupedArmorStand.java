package me.thedivazo.messageoverhead.armorstand;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import me.thedivazo.messageoverhead.util.MinecraftVersion;
import me.thedivazo.messageoverhead.util.Position;
import me.thedivazo.messageoverhead.util.Positionc;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.*;

public class GroupedArmorStand {
    private final MinecraftVersion serverVersion;
    private final ArmorStand.Factory factory;
    private final LineLayout layout;

    private final List<ArmorStand> linesStands = new ArrayList<>();
    private final List<Component> linesTexts = new ArrayList<>();
    private final List<Component> unmodifiableLines = Collections.unmodifiableList(linesTexts);
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

        initializeLines(text);
    }

    public int countLines() {
        return linesTexts.size();
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
        for (int i = 0; i < linesStands.size(); i++) {
            ArmorStand stand = linesStands.get(i);
            stand.show(player);
        }
    }

    public void hide(Player player) {
        Objects.requireNonNull(player, "player");
        if (destroyed) {
            return;
        }
        for (int i = 0; i < linesStands.size(); i++) {
            ArmorStand stand = linesStands.get(i);
            stand.hide(player);
        }
    }

    public void updatePosition(Player player) {
        for (int i = 0; i < linesStands.size(); i++) {
            ArmorStand stand = linesStands.get(i);
            stand.updatePosition(player);
        }
    }

    public List<Component> getLines() {
        return unmodifiableLines;
    }

    public Component getLine(int index) {
        return linesTexts.get(index);
    }

    public void setLines(List<Component> lines) {
        ensureActive();

        List<Component> nextLines = copyLines(lines);
        int sharedLineCount = Math.min(linesStands.size(), nextLines.size());

        for (int i = 0; i < sharedLineCount; i++) {
            updateLineInternal(i, nextLines.get(i));
        }

        for (int i = linesStands.size(); i < nextLines.size(); i++) {
            addLineInternal(i, nextLines.get(i));
        }

        for (int i = linesStands.size() - 1; i >= nextLines.size(); i--) {
            removeLineInternal(i);
        }

        recalculatePosition();
    }

    public void setLine(int index, Component line) {
        ensureActive();

        updateLineInternal(index, line);
        recalculatePosition();
    }

    public void insertLine(int index, Component line) {
        ensureActive();

        addLineInternal(index, line);
        recalculatePosition();
    }

    public Component removeLine(int index) {
        ensureActive();

        Component removedLine = removeLineInternal(index);
        recalculatePosition();
        return removedLine;
    }

    public void addLine(Component line) {
        insertLine(linesStands.size(), line);
    }

    private void ensureActive() {
        if (destroyed) {
            throw new IllegalStateException("Grouped armor stand has already been destroyed");
        }
    }

    private void initializeLines(List<Component> lines) {
        List<Component> initialLines = copyLines(lines);
        for (int i = 0; i < initialLines.size(); i++) {
            Component line = initialLines.get(i);
            linesTexts.add(line);
            linesStands.add(factory.create(line, serverVersion));
        }
        recalculatePosition();
    }

    private void addLineInternal(int index, Component line) {
        Objects.requireNonNull(line, "line");
        if (index < 0 || index > linesStands.size()) {
            throw new IndexOutOfBoundsException("invalid index: " + index + ", actual size: " + linesStands.size());
        }

        ArmorStand stand = factory.create(line, serverVersion);
        linesStands.add(index, stand);
        linesTexts.add(index, line);
        actionInNextUpdate.add(ArmorStandAction.add(stand));
    }

    private Component removeLineInternal(int index) {
        Component removedLine = linesTexts.remove(index);
        ArmorStand stand = linesStands.remove(index);
        actionInNextUpdate.add(ArmorStandAction.remove(stand));
        return removedLine;
    }

    private void updateLineInternal(int index, Component line) {
        Objects.requireNonNull(line, "line");
        Component previousLine = linesTexts.get(index);
        if (Objects.equals(previousLine, line)) {
            linesTexts.set(index, line);
            return;
        }

        ArmorStand stand = linesStands.get(index);
        stand.setText(line);
        linesTexts.set(index, line);
        actionInNextUpdate.add(ArmorStandAction.update(stand));
    }

    private static List<Component> copyLines(List<Component> lines) {
        Objects.requireNonNull(lines, "lines");
        List<Component> copy = new ArrayList<>(lines.size());
        for (int i = 0; i < lines.size(); i++) {
            copy.add(Objects.requireNonNull(lines.get(i), "lines[" + i + "]"));
        }
        return copy;
    }

    public void update(Player player) {
        updatePosition(player);
        actionInNextUpdate.forEach(actionStand -> actionStand.execute(player));
    }

    public void onEndPlayersUpdate() {
        actionInNextUpdate.clear();
    }

    public void destroy() {
        if (destroyed) {
            return;
        }

        destroyed = true;
        for (int i = 0; i < linesStands.size(); i++) {
            ArmorStand actualStand = linesStands.get(i);
            actualStand.destroy();
        }
        actionInNextUpdate.clear();
        linesStands.clear();
        linesTexts.clear();
    }

    private void recalculatePosition() {
        if (linesStands.isEmpty()) {
            return;
        }

        for (int i = linesStands.size() - 1; i >= 0; i--) {
            ArmorStand actualStand = linesStands.get(i);
            Position linePosition = layout.positionForLine(position, linesStands.size() - 1, i);
            actualStand.setPosition(linePosition.x, linePosition.y, linePosition.z);
        }
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
            return lineSpacing * (lastLineIndex - lineIndex);
        }
    }
}
