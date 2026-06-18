package me.thedivazo.messageoverhead;

import me.thedivazo.messageoverhead.util.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MOHPlugin extends JavaPlugin {

    public static MinecraftVersion SERVER_VERSION = MinecraftVersion.parse(Bukkit.getMinecraftVersion());

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}