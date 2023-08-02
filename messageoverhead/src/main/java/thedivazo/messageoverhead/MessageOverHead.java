package thedivazo.messageoverhead;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandContexts;
import lombok.Getter;
import thedivazo.messageoverhead.bubble.BubbleGenerator;
import thedivazo.messageoverhead.bubble.BubbleGeneratorManager;
import thedivazo.messageoverhead.command.AdminCommands;
import thedivazo.messageoverhead.command.DefaultCommands;
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
import org.bukkit.scheduler.BukkitRunnable;
import thedivazo.messageoverhead.config.ConfigManager;
import thedivazo.messageoverhead.util.VersionWrapper;
import thedivazo.messageoverhead.metrics.MetricsManager;
import thedivazo.messageoverhead.util.ConfigWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

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
public class MessageOverHead extends JavaPlugin {

    public static final VersionWrapper PLUGIN_VERSION = VersionWrapper.valueOf("4.0.0");
    public static final VersionWrapper MINECRAFT_VERSION = VersionWrapper.valueOf(Bukkit.getVersion(), Pattern.compile("\\(MC: ([0-9]+)\\.([0-9]+)\\.([0-9]+)"), 1, 2, 3);

    @Getter
    public static final String NAME = "MessageOverHead";

    private static ConfigManager configManager = new ConfigManager();

    public static ConfigManager getConfigManager() {
        return MessageOverHead.configManager;
    }

    public static MessageOverHead getInstance() {
        return JavaPlugin.getPlugin(MessageOverHead.class);
    }

    public static String getLastVersionOfPlugin() {
        String inputLine;
        Logger.info("Check updates...");
        try {
            URL obj = new URL("https://api.spigotmc.org/legacy/update.php?resource=100051");
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (IOException e) {
            Logger.warn("ERROR GETTING LAST VERSION!");
            return "error";
        }
    }

    @Override
    public void onEnable() {
        Logger.init(new JULHandler(getLogger()));
        Logger.info("Starting...");

        saveDefaultConfig();
        try {
            configManager.loadConfig(new ConfigWrapper(getConfig()));
        } catch (InvalidConfigurationException e) {
            Logger.error("Check the configuration for errors or typos");
            Logger.error(e.getMessage(), e);
            Bukkit.getPluginManager().disablePlugin(MessageOverHead.getInstance());
            return;
        }

        checkPluginVersion();
        new MetricsManager(this);
        registerListeners();
        registerCommands();
    }

    private void checkPluginVersion() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!MessageOverHead.PLUGIN_VERSION.equals(getLastVersionOfPlugin())) {
                    for (int i = 0; i < 5; i++) {
                        Logger.warn("PLEASE, UPDATE MESSAGE OVER HEAR! LINK: https://www.spigotmc.org/resources/messageoverhead-pop-up-messages-above-your-head-1-13-1-18.100051/");
                    }
                } else {
                    Logger.info("Plugin have last version");
                }
            }
        }.runTaskAsynchronously(this);
    }

    private void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);

        CommandContexts<BukkitCommandExecutionContext> commandContexts = manager.getCommandContexts();

        commandContexts.registerContext(BubbleGenerator.class, command -> {
            String generatorName = command.popFirstArg();
            BubbleGeneratorManager bubbleGeneratorManager = MessageOverHead.getConfigManager().getBubbleManager().getBubbleGeneratorManager();
            return bubbleGeneratorManager.getBubbleGenerator(generatorName)
                    .orElseThrow(() -> new IllegalArgumentException("Bubble Model not found. Please check the correctness of the data"));
        });

        manager.registerCommand(new DefaultCommands());
        manager.registerCommand(new AdminCommands());

        manager.setDefaultExceptionHandler((command, registeredCommand, sender, args, t)-> {
            getLogger().warning("Error occurred while executing command "+command.getName());
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
        configManager.loadConfig(new ConfigWrapper(MessageOverHead.getInstance().getConfig()));
    }


}

