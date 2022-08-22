package thedivazo;

import api.logging.Logger;
import api.logging.handlers.JULHandler;
import co.aikar.commands.PaperCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.dependency.SoftDependency;
import org.bukkit.plugin.java.annotation.dependency.SoftDependsOn;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import thedivazo.bubblemessagemanager.BubbleMessageManager;
import thedivazo.bubblemessagemanager.DefaultBubbleMessageManager;
import thedivazo.commands.DebugCommands;
import thedivazo.commands.DefaultCommands;
import thedivazo.config.ConfigManager;
import thedivazo.metrics.MetricsManager;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static thedivazo.config.ConfigManager.canSeeSuperVanish;

@Plugin(name = "MessageOverHead", version = PluginSettings.version)
@Dependency(value = "ProtocolLib")
@SoftDependsOn(value = {
        @SoftDependency(value = "PlaceholderAPI"),
        @SoftDependency(value = "SuperVanish"),
        @SoftDependency(value = "PremiumVanish"),
        @SoftDependency(value = "ChatControllerRed")
})
@Author(value = "TheDiVaZo")
@ApiVersion(value = ApiVersion.Target.v1_13)
public class MessageOverHear extends JavaPlugin {

    private static ConfigManager configManager;
    private static BubbleMessageManager bubbleMessageManager;

    public static BubbleMessageManager getBubbleMessageManager() {
        return bubbleMessageManager;
    }

    public static void setBubbleMessageManager(BubbleMessageManager bubbleMessageManager) {
        MessageOverHear.bubbleMessageManager = bubbleMessageManager;
    }

    public static ConfigManager getConfigManager() {
        return MessageOverHear.configManager;
    }

    public static MessageOverHear getInstance() {
        return JavaPlugin.getPlugin(MessageOverHear.class);
    }

    private static void setConfigManager(ConfigManager configManager) {
        MessageOverHear.configManager = configManager;
    }
    @Override
    public void onEnable() {
        Logger.init(new JULHandler(getLogger()));
        Logger.info("Starting...");
        setConfigManager(new ConfigManager(MessageOverHear.getInstance()));
        setBubbleMessageManager(new DefaultBubbleMessageManager(this));
        this.checkPluginVersion();
        new MetricsManager(this);
        registerEvent();
        registerEvent();
        registerCommands();
    }

    private void registerEvent() {
        Bukkit.getPluginManager().registerEvents(getConfigManager().getChatEventListener(), this);
    }

    @Override
    public void onDisable() {
        getBubbleMessageManager().removeAllBubbles();
    }

    private void checkPluginVersion() {
        if (!PluginSettings.version.equals(ConfigManager.getLastVersionOfPlugin())) {
            for (int i = 0; i < 5; i++) {
                Logger.warn("PLEASE, UPDATE MESSAGE OVER HEAR! LINK: https://www.spigotmc.org/resources/messageoverhead-pop-up-messages-above-your-head-1-13-1-18.100051/");
            }
        } else {
            Logger.info("Plugin have last version");
        }
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
    }

    public void reloadConfigManager() {
        super.reloadConfig();
        getConfigManager().saveParam();
        updateBubbleMessageManager();
    }

    public void updateBubbleMessageManager() {
        getBubbleMessageManager().setSoundEnable(getConfigManager().isSoundEnable());
        getBubbleMessageManager().setVisibleTextForOwner(getConfigManager().isVisibleTextForOwner());
        getBubbleMessageManager().setDistance(getConfigManager().getDistance());
        getBubbleMessageManager().setSizeLine(getConfigManager().getSizeLine());
        getBubbleMessageManager().setDelay(getConfigManager().getDelay());
        getBubbleMessageManager().setParticleEnable(getConfigManager().isParticleEnable());
        getBubbleMessageManager().setBiasY(getConfigManager().getBiasY());
    }

    public static void createBubbleMessage(Player player, String message, Player showPlayer) {
        createBubbleMessage(player, message, new HashSet<>(){{add(showPlayer);}});
    }

    public static void createBubbleMessage(Player player, String message) {
        createBubbleMessage(player, message, new HashSet<>(Bukkit.getOnlinePlayers()));
    }

    public static void createBubbleMessage(Player player, String message, Set<Player> showPlayers) {
        if(getConfigManager().haveSendPermission(player)) {
            Set<Player> showPlayersFilter = showPlayers.stream().filter(player1 -> getInstance().isPossibleBubbleMessage(player, player1)).collect(Collectors.toSet());
            getBubbleMessageManager().spawnBubble(getBubbleMessageManager().generateBubbleMessage(player, message), showPlayersFilter);
        }
    }

    public boolean isPossibleBubbleMessage(Player player1, Player player2) {

        boolean canSeePlayer = player2.canSee(player1) && player2.getWorld().equals(player1.getWorld());
        boolean isNotInvisiblePlayer = canSeeSuperVanish(player2, player1) && !player1.hasPotionEffect(PotionEffectType.INVISIBILITY);
        boolean isBeside = canSeePlayer;
        if(isBeside) {
            isBeside = player2.getLocation().distance(player1.getLocation()) < getConfigManager().getDistance();
        }
        boolean isSpectator = player1.getGameMode().equals(GameMode.SPECTATOR);
        return MessageOverHear.getConfigManager().haveSeePermission(player2) && canSeePlayer && isBeside && isNotInvisiblePlayer && !isSpectator;
    }

}

