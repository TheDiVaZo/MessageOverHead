package me.thedivazo.messageoverhead.armorstand;

import me.thedivazo.messageoverhead.core.Viewer;
import me.thedivazo.messageoverhead.core.render.RendererBubble;
import me.thedivazo.messageoverhead.core.render.capability.HeightCapability;
import me.thedivazo.messageoverhead.core.render.capability.PositionCapability;
import me.thedivazo.messageoverhead.core.render.capability.ViewCapability;
import me.thedivazo.messageoverhead.util.Position;
import me.thedivazo.messageoverhead.util.Positionc;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Armor stand implementation of the bubble renderer contracts.
 */
public final class BubbleArmorStand implements RendererBubble, PositionCapability, ViewCapability, HeightCapability {
    private static final double HOLOGRAM_LINE_HEIGHT = 0.289;

    private final GroupedArmorStand armorStand;
    private final Position position;
    private final Set<Viewer> viewers = new HashSet<>();

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
        viewers.forEach(viewer -> armorStand.updatePosition(viewer.getPlayer()));
    }

    @Override
    public Collection<Viewer> viewers() {
        return Collections.unmodifiableSet(viewers);
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
}
