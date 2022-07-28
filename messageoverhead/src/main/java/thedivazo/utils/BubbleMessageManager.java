package thedivazo.utils;

import de.myzelyam.api.vanish.VanishAPI;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import thedivazo.Main;

import java.util.*;


public class BubbleMessageManager {
    private String msg;
    private Player player;
    private Main plugin;
    @Getter
    private static final HashMap<UUID, BubbleMessage> bubbleMessageMap = new HashMap<>();

    private BubbleMessage bubbleMessage;

    public BubbleMessageManager(String msg, Player player, Main plugin) {
        this.msg = msg;
        this.player = player;
        this.plugin = plugin;

        Location loc = player.getLocation();
        loc.setY(loc.getY() + Main.getConfigPlugin().getBiasY());
        bubbleMessage = new BubbleMessage(loc);
    }

    public static void removeOtherBubble(OfflinePlayer player) {
        removeOtherBubble(player.getUniqueId());
    }

    public static void removeOtherBubble(UUID player) {
        if (BubbleMessageManager.getBubbleMessageMap().containsKey(player)) {
            bubbleMessageMap.get(player).remove();
            bubbleMessageMap.remove(player);
        }
    }

    public void removeBubble() {
        if (bubbleMessageMap.get(player.getUniqueId()).equals(bubbleMessage)) {
            bubbleMessageMap.remove(player.getUniqueId());
            bubbleMessage.remove();
        }
    }

    public void generateBubbleMessage() {
        List<String> messageLines = new ArrayList<>();
        List<String> formatLines = getFormatOfPlayer(player);
        for(String format: formatLines) {
            if (Main.getConfigPlugin().isPAPILoaded())
                messageLines.add(ColorString.ofLine(PlaceholderAPI.setPlaceholders(player, format)).replace("%message%", msg));
            else
                messageLines.add(ColorString.ofLine(format).replace("%message%", msg));
        }
        bubbleMessage.init(messageLines);

        if (bubbleMessageMap.containsKey(player.getUniqueId()))
            bubbleMessageMap.get(player.getUniqueId()).remove();

        bubbleMessageMap.put(player.getUniqueId(), bubbleMessage);

        spawnBubble();

        BukkitTask taskMove = new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = player.getLocation().clone();
                loc.setY(loc.getY() + Main.getConfigPlugin().getBiasY());
                bubbleMessage.setPosition(loc);
            }
        }.runTaskTimerAsynchronously(plugin, 1L, 1L);

        BukkitTask taskDelete = new BukkitRunnable() {
            @Override
            public void run() {
                taskMove.cancel();
                removeBubble();
            }
        }.runTaskLaterAsynchronously(plugin, Main.getConfigPlugin().getDelay() * 20L);
        bubbleMessage.removeTask(taskDelete, taskMove);
    }

    public void spawnBubble() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission(Main.getConfigPlugin().getPermSee())) {
                if (onlinePlayer.getWorld().equals(player.getWorld()) && onlinePlayer.canSee(player) && canSeeSuperVanish(onlinePlayer, player) && !player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    if (onlinePlayer.getLocation().distance(player.getLocation()) < Main.getConfigPlugin().getDistance()) {
                        if (onlinePlayer.equals(player)) {
                            if (Main.getConfigPlugin().isVisibleTextForOwner()) {
                                if (Main.getConfigPlugin().isSoundEnable())
                                    bubbleMessage.playSound(onlinePlayer);
                                if (Main.getConfigPlugin().isParticleEnable())
                                    bubbleMessage.playParticle(onlinePlayer);
                                bubbleMessage.show(onlinePlayer);
                            }
                        }
                        else {
                            if (Main.getConfigPlugin().isSoundEnable())
                                bubbleMessage.playSound(onlinePlayer);
                            if (Main.getConfigPlugin().isParticleEnable())
                                bubbleMessage.playParticle(onlinePlayer);
                            bubbleMessage.show(onlinePlayer);
                        }
                    }
                }
            }
        }
    }

    public boolean canSeeSuperVanish(Player viewer, Player viewed) {
        if (Main.getConfigPlugin().isSuperVanishLoaded()) {
            return VanishAPI.canSee(viewer, viewed);
        } else return true;
    }

    public List<String> getFormatOfPlayer(Player player) {
        Integer[] priorityFormat = Main.getConfigPlugin().getMorePermissionFormat().keySet().toArray(new Integer[]{});
        Arrays.sort(priorityFormat);
        List<String> format = new ArrayList<>(){{
            add(Main.getConfigPlugin().getOneDefaultMessageFormat());
        }};

        for (int priority : priorityFormat) {
            String permission = Main.getConfigPlugin().getMorePermissionFormat().get(priority);
            List<String> MaybeFormat = Main.getConfigPlugin().getMoreMessageFormat().get(permission);
            if (permission != null) {
                if (player.hasPermission(permission)) format = MaybeFormat;
            } else {
                format = MaybeFormat;
            }
        }
        return format;
    }
}
