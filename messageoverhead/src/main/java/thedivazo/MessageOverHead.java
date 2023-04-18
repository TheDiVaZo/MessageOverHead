package thedivazo;

import api.logging.Logger;
import api.logging.handlers.JULHandler;
import co.aikar.commands.PaperCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.dependency.SoftDependency;
import org.bukkit.plugin.java.annotation.dependency.SoftDependsOn;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import org.bukkit.scheduler.BukkitRunnable;
import thedivazo.bubblemessagemanager.BubbleMessageManager;
import thedivazo.bubblemessagemanager.DefaultBubbleMessageManager;
import thedivazo.commands.DebugCommands;
import thedivazo.commands.DefaultCommands;
import thedivazo.config.ConfigBubble;
import thedivazo.config.ConfigManager;
import thedivazo.listener.chatlistener.ListenerWrapper;
import thedivazo.metrics.MetricsManager;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Plugin(name = MessageOverHead.NAME, version = MessageOverHead.VERSION)
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
    public static final String VERSION = "3.4";
    public static final String NAME = "MessageOverHead";

    private static ConfigManager configManager;
    private static BubbleMessageManager<Player> bubbleMessageManager;

    public static BubbleMessageManager<Player> getBubbleMessageManager() {
        return bubbleMessageManager;
    }

    public static void setBubbleMessageManager(BubbleMessageManager<Player> bubbleMessageManager) {
        MessageOverHead.bubbleMessageManager = bubbleMessageManager;
    }

    public static ConfigManager getConfigManager() {
        return MessageOverHead.configManager;
    }

    public static MessageOverHead getInstance() {
        return JavaPlugin.getPlugin(MessageOverHead.class);
    }

    private static void setConfigManager(ConfigManager configManager) {
        MessageOverHead.configManager = configManager;
    }
    @Override
    public void onEnable() {
        Logger.init(new JULHandler(getLogger()));
        Logger.info("Starting...");
        setConfigManager(new ConfigManager(MessageOverHead.getInstance()));
        setBubbleMessageManager(new DefaultBubbleMessageManager());
        this.checkPluginVersion();
        new MetricsManager(this);
        registerEvent();
        registerCommands();
    }

    private void registerEvent() {
        getConfigManager().getChatEventListener().disableListener();
        for (ListenerWrapper additionalListener : getConfigManager().getAdditionalListeners()) {
            additionalListener.disableListener();
        }
        if(getConfigManager().isEnableChatListener()) {
            Bukkit.getPluginManager().registerEvents(getConfigManager().getChatEventListener(), this);
        }
        for (ListenerWrapper additionalListener : getConfigManager().getAdditionalListeners()) {
            Bukkit.getPluginManager().registerEvents(additionalListener, this);
        }
    }

    @Override
    public void onDisable() {
        getBubbleMessageManager().removeAllBubbles();
    }

    private void checkPluginVersion() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!MessageOverHead.VERSION.equals(ConfigManager.getLastVersionOfPlugin())) {
                    for (int i = 0; i < 5; i++) {
                        Logger.warn("PLEASE, UPDATE MESSAGE OVER HEAR! LINK: https://www.spigotmc.org/resources/messageoverhead-pop-up-messages-above-your-head-1-13-1-18.100051/");
                    }
                } else {
                    Logger.info("Plugin have last version");
                }
            }
        }.runTaskAsynchronously(this);
    }

    public static Float getVersion() {
        String version = Bukkit.getVersion();
        Pattern pattern = Pattern.compile("\\(MC: ([0-9]+\\.[0-9]+)");
        Matcher matcher = pattern.matcher(version);
        if (matcher.find())
        {
            return Float.parseFloat(matcher.group(1));
        }
        else return null;
    }

    private void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);

        manager.registerCommand(new DefaultCommands());
        manager.registerCommand(new DebugCommands());

        manager.setDefaultExceptionHandler((command, registeredCommand, sender, args, t)-> {
            getLogger().warning("Error occurred while executing command "+command.getName());
            return true;
        });
        manager.getCommandCompletions().registerCompletion("configBubbles", c -> getConfigManager().getConfigBubblesName());
    }

    public void reloadConfigManager() {
        saveDefaultConfig();
        reloadConfig();
        getConfigManager().reloadConfigFile();
        registerEvent();
    }

    public static void createBubbleMessage(ConfigBubble configBubble, Player player, String message, Player showPlayer) {
        createBubbleMessage(configBubble, player, message, new HashSet<>(){{add(showPlayer);}});
    }

    public static void createBubbleMessage(ConfigBubble configBubble,Player player, String message) {
        createBubbleMessage(configBubble,player, message, new HashSet<>(Bukkit.getOnlinePlayers()));
    }

    public static void createBubbleMessage(ConfigBubble configBubble, Player player, String message, Set<Player> showPlayers) {
        if(configBubble.haveSendPermission(player)) {
            Set<Player> showPlayersFilter = showPlayers.stream().filter(player1 -> getInstance().isPossibleBubbleMessage(configBubble,player, player1)).collect(Collectors.toSet());
            getBubbleMessageManager().showBubble(getBubbleMessageManager().generateBubbleMessage(player,configBubble,player, message), showPlayersFilter);
        }
    }

    public boolean isPossibleBubbleMessage(ConfigBubble configBubble, Player player1, Player player2) {
        boolean isNormalDistance = false;
        if(player1.getWorld().equals(player2.getWorld())) {
            isNormalDistance = player2.getLocation().distance(player1.getLocation()) < configBubble.getDistance();
        }
        boolean canSee = getConfigManager().getVanishManager().canSee(player2, player1);
        return configBubble.haveSeePermission(player2) && isNormalDistance && canSee;
    }

}

