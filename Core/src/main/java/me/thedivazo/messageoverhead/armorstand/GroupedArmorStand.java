package me.thedivazo.messageoverhead.armorstand;

import me.thedivazo.messageoverhead.core.text.LegacyTextWrapper;
import me.thedivazo.messageoverhead.util.MinecraftVersion;
import me.thedivazo.messageoverhead.util.Position;
import me.thedivazo.messageoverhead.util.Positionc;
import org.bukkit.entity.Player;

import java.util.*;

public class GroupedArmorStand {
    private final ArmorStand.Factory factory;

    private final List<ArmorStand> stands;

    private final double lineSpacing;
    private final Position position;
    private boolean destroyed;

    public GroupedArmorStand(
            LegacyTextWrapper text,
            ArmorStand.Factory factory,
            double lineSpacing,
            Positionc position,
            MinecraftVersion serverVersion)
    {
        this.stands = new ArrayList<>();
        this.factory = factory;
        this.lineSpacing = lineSpacing;
        this.position = new Position(Objects.requireNonNull(position, "position"));

        LegacyTextWrapper[] lines = text.split("\\R");
        for (LegacyTextWrapper line : lines) {
            stands.add(factory.create(line.buildLegacySectionChar(serverVersion)));
        }
        setPosition(position.x(), position.y(), position.z());
    }

    public int countLines() {
        return stands.size();
    }

    public double getLineSpacing() {
        return lineSpacing;
    }

    public void setPosition(double x, double y, double z) {
        if (destroyed) {
            return;
        }

        position.set(x, y, z);

        double offset = lineSpacing * stands.size();
        for (int i = 0; i < stands.size(); i++) {
            offset -= lineSpacing;
            stands.get(i).setPosition(x, y + offset, z);
        }
    }

    public void show(Player player) {
        Objects.requireNonNull(player, "player");
        if (destroyed) {
            return;
        }
        stands.forEach(stand -> stand.show(player));
    }

    public void hide(Player player) {
        Objects.requireNonNull(player, "player");
        if (destroyed) {
            return;
        }
        stands.forEach(stand -> stand.hide(player));
    }

    public void updatePosition(Player player) {
        stands.forEach(stand -> stand.updatePosition(player));
    }

    public void destroy() {
        if (destroyed) {
            return;
        }

        destroyed = true;
        stands.forEach(ArmorStand::destroy);
    }
}
