package thedivazo.listener;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import thedivazo.BubbleMessage;
import thedivazo.Main;

import java.util.UUID;

public class Listeners implements Listener {
    Main plugin;
    public Listeners(JavaPlugin plugin) {
        this.plugin = (Main) plugin;
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

        if (!player.hasPermission(plugin.permSend)) return;

        Location loc = player.getLocation();

        loc.setY(loc.getY() + plugin.biasY);

        String msg = e.getMessage();
        while (msg.contains("\\")) msg = msg.replace('\\', '/');

        String message;
        if (plugin.isPAPILoaded)
            message = plugin.makeColors(PlaceholderAPI.setPlaceholders(player, plugin.sormat)).replace("%message%", msg);
        else
            message = plugin.makeColors(plugin.sormat).replace("%message%", msg);

        BubbleMessage bubbleMessage = new BubbleMessage(message, loc, plugin);

        if (plugin.soundEnable)
            bubbleMessage.sound(plugin.distance);

        if (plugin.particleEnable)
            bubbleMessage.particle(plugin.distance);

        if (plugin.bubbleMessageMap.containsKey(player.getUniqueId()))
            plugin.bubbleMessageMap.get(player.getUniqueId()).remove();

        plugin.bubbleMessageMap.put(player.getUniqueId(), bubbleMessage);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getLocation().distance(loc) < plugin.distance) {
                if (plugin.isVisibleTextForOwner || !onlinePlayer.equals(player)) {
                    if (onlinePlayer.hasPermission(plugin.permSee))
                        bubbleMessage.spawn(onlinePlayer);
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

}
