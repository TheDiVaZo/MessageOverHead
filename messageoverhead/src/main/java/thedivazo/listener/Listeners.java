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
import thedivazo.utils.BubbleMessage;
import thedivazo.utils.BubbleMessageManager;
import thedivazo.utils.ColorString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class Listeners implements Listener {

    private final Main plugin;

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        BubbleMessageManager.removeOtherBubble(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;
        if (!player.hasPermission(Main.getConfigPlugin().getPermSend())) return;

        BubbleMessageManager bubbleMessageManager = new BubbleMessageManager(e.getMessage(), player, plugin);


        bubbleMessageManager.generateBubbleMessage();
    }


}
