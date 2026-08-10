package me.thedivazo.messageoverhead.armorstand;

import me.thedivazo.messageoverhead.util.MinecraftVersion;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface ArmorStand {
    void show(Player player);

    void updatePosition(Player player);

    void updateMetadata(Player player);

    void hide(Player player);

    void destroy();

    void setPosition(double x, double y, double z);

    void setText(Component text);

    interface Factory {
        ArmorStand create(Component c, MinecraftVersion serverVersion);
    }
}
