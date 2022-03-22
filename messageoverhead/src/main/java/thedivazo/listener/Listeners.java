package thedivazo.listener;

import de.myzelyam.api.vanish.VanishAPI;
import lombok.AllArgsConstructor;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class Listeners implements Listener {

    private final Main plugin;
    private final Config config;

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

        List<String> messageLines = new ArrayList<>();
        List<String> formatLines = getFormatOfPlayer(player);
        for(String format: formatLines) {
            if (config.isPAPILoaded())
                messageLines.add(ColorString.ofLine(PlaceholderAPI.setPlaceholders(player, format)).replace("%message%", msg));
            else
                messageLines.add(ColorString.ofLine(format).replace("%message%", msg));
        }

        BubbleMessage bubbleMessage = new BubbleMessage(messageLines, loc, config);

        if (plugin.getBubbleMessageMap().containsKey(player.getUniqueId()))
            plugin.getBubbleMessageMap().get(player.getUniqueId()).remove();

        plugin.getBubbleMessageMap().put(player.getUniqueId(), bubbleMessage);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission(config.getPermSee())) {
                if (onlinePlayer.getWorld().equals(loc.getWorld()) && onlinePlayer.canSee(player) && canSeeSuperVanish(onlinePlayer, player) && !player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    if (onlinePlayer.getLocation().distance(loc) < config.getDistance()) {
                        if (onlinePlayer.equals(player)) {
                            if (config.isVisibleTextForOwner()) {
                                if (config.isSoundEnable())
                                    bubbleMessage.playSound(onlinePlayer);
                                if (config.isParticleEnable())
                                    bubbleMessage.playParticle(onlinePlayer);
                                bubbleMessage.spawn(onlinePlayer);
                            }
                        }
                        else {
                            if (config.isSoundEnable())
                                bubbleMessage.playSound(onlinePlayer);
                            if (config.isParticleEnable())
                                bubbleMessage.playParticle(onlinePlayer);
                            bubbleMessage.spawn(onlinePlayer);
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

    public List<String> getFormatOfPlayer(Player player) {
        Integer[] priorityFormat = config.getMorePermissionFormat().keySet().toArray(new Integer[]{});
        Arrays.sort(priorityFormat);
        List<String> format = new ArrayList<>(){{
            add(config.getOneDefaultMessageFormat());
        }};

        for (int priority : priorityFormat) {
            String permission = config.getMorePermissionFormat().get(priority);
            List<String> MaybeFormat = config.getMoreMessageFormat().get(permission);
            if (permission != null) {
                if (player.hasPermission(permission)) format = MaybeFormat;
            } else {
                format = MaybeFormat;
            }
        }
        return format;
    }


}
