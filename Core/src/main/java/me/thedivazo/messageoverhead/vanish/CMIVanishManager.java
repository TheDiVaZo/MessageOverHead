package me.thedivazo.messageoverhead.vanish;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CMIVanishManager extends VanishManager {
    public static final String SCOPED_ID = "cmi-vanish";
    private static final String PLUGIN_NAME = "CMI";

    @Override
    public boolean canSee(Player viewer, Player viewed) {
        return !isCmiVanished(viewed);
    }

    @Override
    public boolean isInvisible(Player player) {
        return isCmiVanished(player);
    }

    private boolean isCmiVanished(Player player) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
        if (plugin == null || !plugin.isEnabled()) {
            return false;
        }

        try {
            Object playerManager = plugin.getClass().getMethod("getPlayerManager").invoke(plugin);
            Object user = playerManager.getClass().getMethod("getUser", Player.class).invoke(playerManager, player);
            return user != null && Boolean.TRUE.equals(user.getClass().getMethod("isVanished").invoke(user));
        } catch (ReflectiveOperationException | LinkageError exception) {
            return false;
        }
    }
}
