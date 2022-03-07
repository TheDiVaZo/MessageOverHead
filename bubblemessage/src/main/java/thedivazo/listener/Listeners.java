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
import thedivazo.utils.BubbleMessage;
import thedivazo.Main;

import java.util.Arrays;
import java.util.UUID;

public class Listeners implements Listener {
    Main plugin;
    public Listeners(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        UUID playerUUID = e.getPlayer().getUniqueId();
        if (plugin.bubbleMessageMap.containsKey(playerUUID)) {
            plugin.bubbleMessageMap.get(playerUUID).remove();
            plugin.bubbleMessageMap.remove(playerUUID);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;

        if (!player.hasPermission(plugin.permSend)) return;

        Location loc = player.getLocation();

        loc.setY(loc.getY() + plugin.biasY);

        String msg = e.getMessage();
        while (msg.contains("\\")) msg = msg.replace('\\', '/');

        String message;
        String format = getFormatOfPlayer(player);
        if (plugin.isPAPILoaded)
            message = Main.makeColors(PlaceholderAPI.setPlaceholders(player, format)).replace("%message%", msg);
        else
            message = Main.makeColors(format).replace("%message%", msg);

        BubbleMessage bubbleMessage = new BubbleMessage(message, loc, plugin);

        if (plugin.bubbleMessageMap.containsKey(player.getUniqueId()))
            plugin.bubbleMessageMap.get(player.getUniqueId()).remove();

        plugin.bubbleMessageMap.put(player.getUniqueId(), bubbleMessage);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission(plugin.permSee)) {
                if(onlinePlayer.getWorld().equals(loc.getWorld()) && onlinePlayer.canSee(player) && canSeeSuperVanish(onlinePlayer, player) && !player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    if (onlinePlayer.getLocation().distance(loc) < plugin.distance) {
                        if (plugin.isVisibleTextForOwner || !onlinePlayer.equals(player)) {
                            bubbleMessage.spawn(onlinePlayer);
                            if (plugin.soundEnable)
                                bubbleMessage.sound(onlinePlayer);

                            if (plugin.particleEnable)
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
                loc.setY(loc.getY() + plugin.biasY);
                bubbleMessage.setPosition(loc);
            }
        }.runTaskTimer(plugin, 1L, 1L);

        BukkitTask taskDelete = new BukkitRunnable() {
            @Override
            public void run() {
                taskMove.cancel();
                if (plugin.bubbleMessageMap.get(player.getUniqueId()).equals(bubbleMessage)) {
                    plugin.bubbleMessageMap.remove(player.getUniqueId());
                    bubbleMessage.remove();
                }
            }
        }.runTaskLater(plugin, plugin.delay * 20L);
        bubbleMessage.removeTask(taskDelete, taskMove);
    }

    public boolean canSeeSuperVanish(Player viewer, Player viewed) {
        if(plugin.isSuperVanishLoaded) {
            return VanishAPI.canSee(viewer, viewed);
        }
        else return true;
    }

    public String getFormatOfPlayer(Player player) {
        int[] priorityFormat = Main.permissionFormat.keySet().stream().mapToInt(x->x).toArray();
        Arrays.sort(priorityFormat);
        String format = Main.format;

        for(int priority: priorityFormat) {
            String[] permissionAndFormat = Main.permissionFormat.get(priority);
            if(permissionAndFormat[0] != null) {
                if (player.hasPermission(permissionAndFormat[0])) {
                    format = permissionAndFormat[1];
                }
            }
            else {
                format = permissionAndFormat[1];
            }
        }
        return format;
    }



}
