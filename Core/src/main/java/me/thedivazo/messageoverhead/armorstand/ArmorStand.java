package me.thedivazo.messageoverhead.armorstand;

import me.thedivazo.messageoverhead.core.text.LegacyTextWrapper;
import org.bukkit.entity.Player;

public interface ArmorStand {
    void show(Player player);

    void updatePosition(Player player);

    void updateMetadata(Player player);

    void hide(Player player);

    void destroy();

    void setPosition(double x, double y, double z);

    void setText(String legacyColoredText);
    interface Factory {
        ArmorStand create(String legacyColoredText);
    }
}
