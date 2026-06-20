package me.thedivazo.messageoverhead.armorstand;

import me.thedivazo.messageoverhead.MessageOverHeadPlugin;
import me.thedivazo.messageoverhead.core.Message;
import me.thedivazo.messageoverhead.core.render.RendererBubble;
import me.thedivazo.messageoverhead.core.render.capability.RendererPosition;
import me.thedivazo.messageoverhead.core.render.capability.RendererView;
import me.thedivazo.messageoverhead.util.MinecraftVersion;
import me.thedivazo.messageoverhead.util.Position;
import me.thedivazo.messageoverhead.util.Positionc;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Armor stand implementation of the bubble renderer contracts.
 */
public final class BubbleArmorStand implements RendererBubble, RendererPosition, RendererView {
    private final FakeArmorStand armorStand;
    private final Position position;

    private boolean destroyed;

    public BubbleArmorStand(String message, Positionc positionc) {
        Objects.requireNonNull(positionc, "positionc");
        this.armorStand = new FakeArmorStand(
                Objects.requireNonNull(message, "message"),
                new Location(null, positionc.x(), positionc.y(), positionc.z())
        );
        this.position = new Position(positionc);
    }

    @Override
    public void show(Player player) {
        Objects.requireNonNull(player, "player");
        ensureActive();
        armorStand.show(player);
    }

    @Override
    public void hide(Player player) {
        Objects.requireNonNull(player, "player");
        if (destroyed) {
            return;
        }
        armorStand.hide(player);
    }

    @Override
    public void update(Player player) {
        Objects.requireNonNull(player, "player");
        if (destroyed) {
            return;
        }
        armorStand.updatePosition(player);
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
}
