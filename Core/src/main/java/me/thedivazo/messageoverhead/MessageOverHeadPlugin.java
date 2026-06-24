package me.thedivazo.messageoverhead;

import me.thedivazo.messageoverhead.api.ComponentService;
import me.thedivazo.messageoverhead.api.SpawnService;
import me.thedivazo.messageoverhead.command.BubbleTestCommand;
import me.thedivazo.messageoverhead.core.BubbleContainer;
import me.thedivazo.messageoverhead.core.OnlinePlayerProvider;
import me.thedivazo.messageoverhead.core.tick.BubbleScheduler;
import me.thedivazo.messageoverhead.core.component.ComponentRegistry;
import me.thedivazo.messageoverhead.core.tick.BukkitBubbleScheduler;
import me.thedivazo.messageoverhead.util.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public class MessageOverHeadPlugin extends JavaPlugin {
    public static final OnlinePlayerProvider DEFAULT_ONLINE_PLAYER_PROVIDER = new OnlinePlayerProvider();
    private static MessageOverHeadPlugin INSTANCE;

    public static MessageOverHeadPlugin getInstance() {
        return INSTANCE;
    }

    public static final MinecraftVersion SERVER_VERSION = MinecraftVersion.parse(Bukkit.getMinecraftVersion());

    private final ComponentRegistry componentRegistry = new ComponentRegistry();
    private BubbleContainer bubbleContainer;
    private BubbleScheduler bubbleScheduler = new BukkitBubbleScheduler(this);
    private ComponentService componentService;

    private SpawnService spawnService;
    private LegacyPaperCommandManager<CommandSender> commandManager;

    @Override
    public void onEnable() {
        if (INSTANCE != null) throw new IllegalStateException("Already initialized!");
        INSTANCE = this;

        this.bubbleContainer = new BubbleContainer();
        this.componentService = new ComponentService(componentRegistry, bubbleContainer);

        this.spawnService = new SpawnService(
                bubbleContainer,
                bubbleScheduler
        );

        commandManager = LegacyPaperCommandManager.createNative(
                this,
                ExecutionCoordinator.simpleCoordinator()
        );
        BubbleTestCommand.register(commandManager, spawnService, componentRegistry);
    }

    @Override
    public void onDisable() {
        try {
            if (spawnService != null) {
                spawnService.close();
            }
        } finally {
            commandManager = null;
            spawnService = null;
            bubbleContainer = null;
            INSTANCE = null;
        }
    }

    public ComponentService getComponentService() {
        return componentService;
    }

    public SpawnService getSpawnService() {
        return spawnService;
    }
}
