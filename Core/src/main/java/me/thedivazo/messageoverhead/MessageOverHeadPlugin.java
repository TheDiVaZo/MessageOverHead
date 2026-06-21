package me.thedivazo.messageoverhead;

import me.thedivazo.messageoverhead.armorstand.ArmorStandBubbleFactory;
import me.thedivazo.messageoverhead.command.BubbleTestCommand;
import me.thedivazo.messageoverhead.core.BubbleContainer;
import me.thedivazo.messageoverhead.profile.BubbleManager;
import me.thedivazo.messageoverhead.profile.ImmutableBubbleManager;
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

import java.util.Map;

public class MessageOverHeadPlugin extends JavaPlugin {
    private static MessageOverHeadPlugin INSTANCE;

    public static MessageOverHeadPlugin getInstance() {
        return INSTANCE;
    }

    public static final MinecraftVersion SERVER_VERSION = MinecraftVersion.parse(Bukkit.getMinecraftVersion());

    private final ComponentRegistry componentRegistry = new ComponentRegistry();
    private BubbleContainer bubbleContainer;
    private ComponentService componentService;

    private BubbleManager bubbleManager;
    private LegacyPaperCommandManager<CommandSender> commandManager;

    @Override
    public void onEnable() {
        if (INSTANCE != null) throw new IllegalStateException("Already initialized!");
        INSTANCE = this;

        this.bubbleContainer = new BubbleContainer(new BukkitBubbleScheduler(this, 0, 1));
        this.componentService = new ComponentService(componentRegistry, bubbleContainer);

        this.bubbleManager = new ImmutableBubbleManager(
                new DefaultBubbleFactory(componentRegistry, new ArmorStandBubbleFactory()),
                bubbleContainer,
                Map.of(
                        PositionComponent.key(), context -> {
                            PositionComponent component = PositionComponent.Factory.INSTANCE.create(context);
                            component.add(pos -> pos.y += 2.2);
                            return component;
                        },
                        ViewComponent.key(), ViewComponent.factory(new ViewComponent.Settings(20, 5)),
                        LifetimeComponent.key(), LifetimeComponent.factory(20*6)
                )
        );

        commandManager = LegacyPaperCommandManager.createNative(
                this,
                ExecutionCoordinator.simpleCoordinator()
        );
        BubbleTestCommand.register(commandManager, bubbleManager);
    }

    @Override
    public void onDisable() {
        try {
            if (bubbleManager != null) {
                bubbleManager.close();
            }
        } finally {
            commandManager = null;
            bubbleManager = null;
            bubbleContainer = null;
            INSTANCE = null;
        }
    }

    public ComponentService getComponentService() {
        return componentService;
    }

    public BubbleManager getBubbleManager() {
        return bubbleManager;
    }
}
