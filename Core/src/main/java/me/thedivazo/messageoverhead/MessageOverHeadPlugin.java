package me.thedivazo.messageoverhead;

import me.thedivazo.messageoverhead.core.component.ComponentRegistry;
import me.thedivazo.messageoverhead.util.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MessageOverHeadPlugin extends JavaPlugin {
    private static MessageOverHeadPlugin INSTANCE;

    public static MessageOverHeadPlugin getInstance() {
        return INSTANCE;
    }

    public static final MinecraftVersion SERVER_VERSION = MinecraftVersion.parse(Bukkit.getMinecraftVersion());

    private final ComponentRegistry componentRegistry = new ComponentRegistry();
    private final ComponentService componentService = new ComponentService(componentRegistry);

    @Override
    public void onEnable() {
        if (INSTANCE != null) throw new IllegalStateException("Already initialized!");
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        INSTANCE = null;
    }

    public ComponentService getComponentService() {
        return componentService;
    }
}
