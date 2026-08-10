package me.thedivazo.messageoverhead.armorstand;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.thedivazo.messageoverhead.core.Viewer;
import me.thedivazo.messageoverhead.core.render.RendererBubble;
import me.thedivazo.messageoverhead.core.render.capability.HeightCapability;
import me.thedivazo.messageoverhead.core.render.capability.PositionCapability;
import me.thedivazo.messageoverhead.core.render.capability.TextCapability;
import me.thedivazo.messageoverhead.core.render.capability.ViewCapability;
import me.thedivazo.messageoverhead.util.Position;
import me.thedivazo.messageoverhead.util.Positionc;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Armor stand implementation of the bubble renderer contracts.
 */
public final class BubbleArmorStand implements RendererBubble, PositionCapability, ViewCapability, HeightCapability, TextCapability {
    private static final double HOLOGRAM_LINE_HEIGHT = 0.289;

    private final GroupedArmorStand armorStand;
    private final Position position;
    private final Set<Viewer> viewers = new ObjectOpenHashSet<>();

    private boolean destroyed;

    public BubbleArmorStand(GroupedArmorStand armorStand, Positionc positionc) {
        Objects.requireNonNull(positionc, "positionc");
        this.armorStand = armorStand;
        this.position = new Position(positionc);
    }

    @Override
    public void show(Viewer player) {
        Objects.requireNonNull(player, "player");
        ensureActive();
        if (viewers.add(player)) {
            armorStand.show(player.getPlayer());
        }
    }

    @Override
    public void hide(Viewer player) {
        Objects.requireNonNull(player, "player");
        if (destroyed) {
            return;
        }
        if (viewers.remove(player)) {
            armorStand.hide(player.getPlayer());
        }
    }

    @Override
    public void updateAll() {
        viewers.forEach(viewer -> armorStand.update(viewer.getPlayer()));
        armorStand.onEndPlayersUpdate();
    }

    private final Collection<Viewer> unmodifiableViewers = Collections.unmodifiableCollection(viewers);

    @Override
    public Collection<Viewer> viewers() {
        return unmodifiableViewers;
    }

    @Override
    public void setPosition(double x, double y, double z) {
        ensureActive();
        position.set(x, y, z);
        armorStand.setPosition(x, y, z);
    }

    @Override
    public Positionc getPosition() {
        return position;
    }

    @Override
    public void destroy() {
        if (destroyed) {
            return;
        }
        destroyed = true;
        armorStand.destroy();
    }

    @Override
    public <T> @Nullable T capabilityOrNull(Class<T> type) {
        Objects.requireNonNull(type, "type");
        return type.isInstance(this) ? type.cast(this) : null;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    private void ensureActive() {
        if (destroyed) {
            throw new IllegalStateException("Bubble armor stand has already been destroyed");
        }
    }

    @Override
    public double getHeight() {
        return (Math.max(0, armorStand.countLines()-1)*armorStand.getLineSpacing()) + HOLOGRAM_LINE_HEIGHT;
    }

    @Override
    public Component getLine(int index) {
        return armorStand.getLine(index);
    }

    @Override
    public List<Component> getLines() {
        return armorStand.getLines();
    }

    @Override
    public void setLines(List<Component> components) {
        ensureActive();
        armorStand.setLines(components);
    }

    @Override
    public void insertLine(int index, Component component) {
        ensureActive();
        armorStand.insertLine(index, component);
    }

    @Override
    public Component removeLine(int index) {
        ensureActive();
        return armorStand.removeLine(index);
    }

    @Override
    public void setLine(int index, Component component) {
        ensureActive();
        armorStand.setLine(index, component);
    }

    @Override
    public void addLine(Component component) {
        ensureActive();
        armorStand.addLine(component);
    }
}
