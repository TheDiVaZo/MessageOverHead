package me.thedivazo.messageoverhead.vanish;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class EssentialsVanishManager extends VanishManager {
    public static final String SCOPED_ID = "essentials-vanish";
    private static final String PLUGIN_NAME = "Essentials";

    @Override
    public boolean canSee(Player viewer, Player viewed) {
        return !isEssentialsVanished(viewed);
    }

    @Override
    public boolean isInvisible(Player player) {
        return isEssentialsVanished(player);
    }

    private boolean isEssentialsVanished(Player player) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
        if (plugin == null || !plugin.isEnabled()) {
            return false;
        }

        try {
            Object user = plugin.getClass().getMethod("getUser", Player.class).invoke(plugin, player);
            return user != null && Boolean.TRUE.equals(user.getClass().getMethod("isVanished").invoke(user));
        } catch (ReflectiveOperationException | LinkageError exception) {
            return false;
        }
    }
}
