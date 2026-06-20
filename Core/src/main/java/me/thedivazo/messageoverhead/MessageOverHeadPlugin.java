package me.thedivazo.messageoverhead;

import me.thedivazo.messageoverhead.armorstand.ArmorStandBubbleFactory;
import me.thedivazo.messageoverhead.command.BubbleTestCommand;
import me.thedivazo.messageoverhead.core.BubbleManager;
import me.thedivazo.messageoverhead.core.DefaultBubbleFactory;
import me.thedivazo.messageoverhead.core.component.ComponentRegistry;
import me.thedivazo.messageoverhead.core.component.LifetimeComponent;
import me.thedivazo.messageoverhead.core.component.PositionComponent;
import me.thedivazo.messageoverhead.core.component.ViewComponent;
import me.thedivazo.messageoverhead.core.tick.BukkitBubbleScheduler;
import me.thedivazo.messageoverhead.util.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public class MessageOverHeadPlugin extends JavaPlugin {
    private static MessageOverHeadPlugin INSTANCE;

    public static MessageOverHeadPlugin getInstance() {
        return INSTANCE;
    }

    public static final MinecraftVersion SERVER_VERSION = MinecraftVersion.parse(Bukkit.getMinecraftVersion());

    private final ComponentRegistry componentRegistry = new ComponentRegistry();
    private final ComponentService componentService = new ComponentService(componentRegistry);

    private final BubbleManager bubbleManager = new BubbleManager(new DefaultBubbleFactory(componentRegistry, new ArmorStandBubbleFactory()), new BukkitBubbleScheduler(this, 0, 1));
    private LegacyPaperCommandManager<CommandSender> commandManager;

    @Override
    public void onEnable() {
        if (INSTANCE != null) throw new IllegalStateException("Already initialized!");
        INSTANCE = this;

        bubbleManager.addPreCreateCallback(
                activeBubble -> PositionComponent.attach(activeBubble).add(pos -> {
                    pos.y += 2.2;
                })
        );
        bubbleManager.addPreCreateCallback(
                bubble -> ViewComponent.attach(bubble,
                        ViewComponent.factory(
                                new ViewComponent.Settings(20, 5)
                        )
                )
        );
        bubbleManager.addPreCreateCallback(
                bubble -> LifetimeComponent.attach(bubble, 20*6)
        );

        commandManager = LegacyPaperCommandManager.createNative(
                this,
                ExecutionCoordinator.simpleCoordinator()
        );
        BubbleTestCommand.register(commandManager, bubbleManager);
    }

    @Override
    public void onDisable() {
        commandManager = null;
        INSTANCE = null;
    }

    public ComponentService getComponentService() {
        return componentService;
    }

    public BubbleManager getBubbleManager() {
        return bubbleManager;
    }
}
