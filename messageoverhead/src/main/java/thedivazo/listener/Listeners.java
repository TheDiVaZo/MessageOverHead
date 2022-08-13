package thedivazo.listener;

import lombok.AllArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import thedivazo.utils.BubbleMessageManager;

@AllArgsConstructor
public class Listeners implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        BubbleMessageManager.removeOtherBubble(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;
        if (!player.hasPermission("moh.send")) return;

        BubbleMessageManager bubbleMessageManager = new BubbleMessageManager(e.getMessage(), player);
        bubbleMessageManager.generateBubbleMessage();
    }


}
