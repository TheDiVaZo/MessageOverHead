package me.thedivazo.messageoverhead;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MOHPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getScheduler();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}