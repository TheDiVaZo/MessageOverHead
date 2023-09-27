package thedivazo.messageoverhead;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandContexts;
import lombok.Getter;
import thedivazo.messageoverhead.bubble.BubbleGenerator;
import thedivazo.messageoverhead.bubble.BubbleGeneratorManager;
import thedivazo.messageoverhead.bubble.BubbleManager;
import thedivazo.messageoverhead.channel.Channel;
import thedivazo.messageoverhead.channel.ChannelFactory;
import thedivazo.messageoverhead.command.AdminCommands;
import thedivazo.messageoverhead.command.DebugCommands;
import thedivazo.messageoverhead.command.DefaultCommands;
import thedivazo.messageoverhead.bubble.BubbleModel;
import thedivazo.messageoverhead.config.ConfigAdapter;
import thedivazo.messageoverhead.integration.IntegrationManager;
import thedivazo.messageoverhead.logging.Logger;
import thedivazo.messageoverhead.logging.handlers.JULHandler;
import co.aikar.commands.PaperCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.dependency.SoftDependency;
import org.bukkit.plugin.java.annotation.dependency.SoftDependsOn;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import thedivazo.messageoverhead.util.VersionWrapper;
import thedivazo.messageoverhead.metrics.MetricsManager;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Plugin(name = MessageOverHead.NAME, version = "4.0.0")
@Dependency(value = "ProtocolLib")
@SoftDependsOn(value = {
        @SoftDependency(value = "PlaceholderAPI"),
        @SoftDependency(value = "SuperVanish"),
        @SoftDependency(value = "PremiumVanish"),
        @SoftDependency(value = "ChatControllerRed"),
        @SoftDependency(value = "Essentials"),
        @SoftDependency(value = "CMI")
})
@Author(value = "TheDiVaZo")
@ApiVersion(value = ApiVersion.Target.v1_13)
@Getter
public class MessageOverHead extends JavaPlugin {
    public static final VersionWrapper MINECRAFT_VERSION = VersionWrapper.valueOf(Bukkit.getVersion());

    public static final String NAME = "MessageOverHead";

    @Getter
    private static ConfigAdapter configAdapter;

    @Getter
    private static BubbleManager bubbleManager;

    public static MessageOverHead getInstance() {
        return JavaPlugin.getPlugin(MessageOverHead.class);
    }

    @Override
    public void onEnable() {
        Logger.init(new JULHandler(getLogger()));
        Logger.info("Starting...");
        if (!IntegrationManager.isProtocolLib()) {
            Logger.error("Download ProtocolLib!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();
        try {
            reloadConfigManager(false);
            updateBubbleManager();
        } catch (InvalidConfigurationException e) {
            Logger.error("Check the configuration for errors or typos");
            Logger.error(e.getMessage(), e);
            Bukkit.getPluginManager().disablePlugin(MessageOverHead.getInstance());
            return;
        }

        new MetricsManager(this);
        registerListeners();
        registerCommands();
    }

    private static void updateBubbleManager() {
        LinkedHashSet<BubbleGenerator> bubbleGeneratorSet = getConfigAdapter().getBubbleModels().stream()
                .map(BubbleGenerator::new).collect(Collectors.toCollection(LinkedHashSet::new));
        BubbleGeneratorManager bubbleGeneratorManager = new BubbleGeneratorManager(bubbleGeneratorSet);
        bubbleManager = new BubbleManager(bubbleGeneratorManager);
    }

    private void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);

        CommandContexts<BukkitCommandExecutionContext> commandContexts = manager.getCommandContexts();

        commandContexts.registerContext(BubbleGenerator.class, command -> {
            String generatorName = command.popFirstArg();
            BubbleGeneratorManager bubbleGeneratorManager = bubbleManager.getBubbleGeneratorManager();
            return bubbleGeneratorManager.getBubbleGenerator(generatorName)
                    .orElseThrow(() -> new IllegalArgumentException("Bubble Model not found. Please check the correctness of the data"));
        });
        manager.getCommandContexts().registerContext(Channel.class, c -> {
            String channel = c.popFirstArg();
            return ChannelFactory.create(channel);
        });

        manager.registerCommand(new DefaultCommands());
        manager.registerCommand(new AdminCommands());
        manager.registerCommand(new DebugCommands());

        manager.getCommandCompletions().registerCompletion("bubble-generators", context -> configAdapter.getBubbleModels().stream()
                .map(BubbleModel::getName)
                .collect(Collectors.toList()));
        manager.getCommandCompletions().registerCompletion("channels", context -> List.of("#all", "#private", "#command"));

        manager.setDefaultExceptionHandler((command, registeredCommand, sender, args, t) -> {
            getLogger().warning("Error occurred while executing command " + command.getName());
            return true;
        });
    }

    private void registerListeners() {
        IntegrationManager.getChatListeners().forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
        IntegrationManager.getVanishListeners().forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    public static void reloadConfigManager(boolean isReloadConfigFromDisk) throws InvalidConfigurationException {
        if (isReloadConfigFromDisk) {
            MessageOverHead.getInstance().reloadConfig();
            MessageOverHead.getInstance().saveConfig();
        }
        if (Objects.isNull(configAdapter)) configAdapter = new ConfigAdapter(MessageOverHead.getInstance().getConfig());
        else getConfigAdapter().updateConfiguration(MessageOverHead.getInstance().getConfig());
    }

    @Override
    public void onDisable() {
        bubbleManager.removeAllBubbles();
    }
}

