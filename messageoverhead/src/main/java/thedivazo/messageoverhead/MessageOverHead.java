package thedivazo.messageoverhead;

import thedivazo.messageoverhead.api.logging.Logger;
import thedivazo.messageoverhead.api.logging.handlers.JULHandler;
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
import thedivazo.messageoverhead.listener.chatlistener.DefaultChatListener;
import thedivazo.messageoverhead.utils.VersionWrapper;
import thedivazo.messageoverhead.vanish.VanishWrapperManager;
import thedivazo.messageoverhead.metrics.MetricsManager;
import thedivazo.messageoverhead.utils.ConfigWrapper;

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
    public static final VersionWrapper MINECRAFT_VERSION = VersionWrapper.valueOf(Bukkit.getVersion(), Pattern.compile("\\(MC: ([0-9]+)\\.([0-9]+)\\.([0-9]+)"), 0, 1, 2);

    public static final String NAME = "MessageOverHead";

    private static ConfigManager configManager = new ConfigManager();

    private static VanishWrapperManager vanishWrapperManager = new VanishWrapperManager();

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
            Logger.error(e.getMessage(), e);
            Bukkit.getPluginManager().disablePlugin(MessageOverHead.getInstance());
            return;
        }
        vanishWrapperManager.loadVanish();

        checkPluginVersion();
        new MetricsManager(this);
        registerEvent();
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

    private void registerEvent() {
        new DefaultChatListener().register();
    }

    private void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);

        //manager.registerCommand(new DefaultCommands());
        //manager.registerCommand(new DebugCommands());

        manager.setDefaultExceptionHandler((command, registeredCommand, sender, args, t)-> {
            getLogger().warning("Error occurred while executing command "+command.getName());
            return true;
        });
        //manager.getCommandCompletions().registerCompletion("configBubbles", c -> getConfigManager().getConfigBubblesName());
    }


}

