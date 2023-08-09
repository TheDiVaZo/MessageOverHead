package thedivazo.messageoverhead;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandContexts;
import lombok.Getter;
import thedivazo.messageoverhead.bubble.BubbleGenerator;
import thedivazo.messageoverhead.bubble.BubbleGeneratorManager;
import thedivazo.messageoverhead.command.AdminCommands;
import thedivazo.messageoverhead.command.DefaultCommands;
import thedivazo.messageoverhead.bubble.BubbleModel;
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
import thedivazo.messageoverhead.config.ConfigManager;
import thedivazo.messageoverhead.util.VersionWrapper;
import thedivazo.messageoverhead.metrics.MetricsManager;
import thedivazo.messageoverhead.util.ConfigWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public static final ConfigManager CONFIG_MANAGER = new ConfigManager();

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
            CONFIG_MANAGER.loadConfig(new ConfigWrapper(getConfig()));
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

    private void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);

        CommandContexts<BukkitCommandExecutionContext> commandContexts = manager.getCommandContexts();

        commandContexts.registerContext(BubbleGenerator.class, command -> {
            String generatorName = command.popFirstArg();
            BubbleGeneratorManager bubbleGeneratorManager = MessageOverHead.CONFIG_MANAGER.getBubbleManager().getBubbleGeneratorManager();
            return bubbleGeneratorManager.getBubbleGenerator(generatorName)
                    .orElseThrow(() -> new IllegalArgumentException("Bubble Model not found. Please check the correctness of the data"));
        });

        manager.registerCommand(new DefaultCommands());
        manager.registerCommand(new AdminCommands());

        manager.getCommandCompletions().registerCompletion("bubble-generators", context -> CONFIG_MANAGER.getBubbleModelSet().stream()
                .map(BubbleModel::getName)
                .collect(Collectors.toList()));

        manager.setDefaultExceptionHandler((command, registeredCommand, sender, args, t) -> {
            getLogger().warning("Error occurred while executing command " + command.getName());
            return true;
        });
    }

    private void registerListeners() {
        IntegrationManager.getChatListeners().forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
        IntegrationManager.getVanishListeners().forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    public static void reloadConfigManager() throws InvalidConfigurationException {
        MessageOverHead.getInstance().reloadConfig();
        MessageOverHead.getInstance().saveConfig();
        CONFIG_MANAGER.loadConfig(new ConfigWrapper(MessageOverHead.getInstance().getConfig()));
    }

    @Override
    public void onDisable() {
        CONFIG_MANAGER.getBubbleManager().removeAllBubbles();
    }
}

