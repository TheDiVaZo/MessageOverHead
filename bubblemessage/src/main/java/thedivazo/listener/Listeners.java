package thedivazo.listener;

import de.myzelyam.api.vanish.VanishAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import thedivazo.Main;
import thedivazo.config.Config;
import thedivazo.utils.BubbleMessage;
import thedivazo.utils.ColorString;

import java.util.Arrays;
import java.util.UUID;

public class Listeners implements Listener {

    private final Main plugin;
    private final Config config;

    public Listeners(Main plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        UUID playerUUID = e.getPlayer().getUniqueId();
        if (plugin.getBubbleMessageMap().containsKey(playerUUID)) {
            plugin.getBubbleMessageMap().get(playerUUID).remove();
            plugin.getBubbleMessageMap().remove(playerUUID);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;

        if (!player.hasPermission(config.getPermSend())) return;

        Location loc = player.getLocation();

        loc.setY(loc.getY() + config.getBiasY());

        String msg = e.getMessage();
        while (msg.contains("\\")) msg = msg.replace('\\', '/');

        String message;
        String format = getFormatOfPlayer(player);
        if (config.isPAPILoaded())
            message = ColorString.ofLine(PlaceholderAPI.setPlaceholders(player, format)).replace("%message%", msg);
        else
            message = ColorString.ofLine(format).replace("%message%", msg);

        BubbleMessage bubbleMessage = new BubbleMessage(message, loc, config);

        if (plugin.getBubbleMessageMap().containsKey(player.getUniqueId()))
            plugin.getBubbleMessageMap().get(player.getUniqueId()).remove();

        plugin.getBubbleMessageMap().put(player.getUniqueId(), bubbleMessage);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission(config.getPermSee())) {
                if (onlinePlayer.getWorld().equals(loc.getWorld()) && onlinePlayer.canSee(player) && canSeeSuperVanish(onlinePlayer, player) && !player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    if (onlinePlayer.getLocation().distance(loc) < config.getDistance()) {
                        if (config.isVisibleTextForOwner() || !onlinePlayer.equals(player)) {
                            bubbleMessage.spawn(onlinePlayer);
                            if (config.isSoundEnable())
                                bubbleMessage.sound(onlinePlayer);


                            if (config.isParticleEnable())
                                bubbleMessage.particle(onlinePlayer);
                        }
                    }
                }
            }
        }

        BukkitTask taskMove = new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = player.getLocation().clone();
                loc.setY(loc.getY() + config.getBiasY());
                bubbleMessage.setPosition(loc);
            }
        }.runTaskTimer(plugin, 1L, 1L);

        BukkitTask taskDelete = new BukkitRunnable() {
            @Override
            public void run() {
                taskMove.cancel();
                if (plugin.getBubbleMessageMap().get(player.getUniqueId()).equals(bubbleMessage)) {
                    plugin.getBubbleMessageMap().remove(player.getUniqueId());
                    bubbleMessage.remove();
                }
            }
        }.runTaskLater(plugin, config.getDelay() * 20L);
        bubbleMessage.removeTask(taskDelete, taskMove);
    }

    public boolean canSeeSuperVanish(Player viewer, Player viewed) {
        if (config.isSuperVanishLoaded()) {
            return VanishAPI.canSee(viewer, viewed);
        } else return true;
    }

    public String getFormatOfPlayer(Player player) {
        int[] priorityFormat = config.getPermissionFormat().keySet().stream().mapToInt(x -> x).toArray();
        Arrays.sort(priorityFormat);
        String format = config.getMessageFormat();

        for (int priority : priorityFormat) {
            String[] permissionAndFormat = config.getPermissionFormat().get(priority);
            if (permissionAndFormat[0] != null) {
                if (player.hasPermission(permissionAndFormat[0])) {
                    format = permissionAndFormat[1];
                }
            } else {
                format = permissionAndFormat[1];
            }
        }
        return format;
    }


}
