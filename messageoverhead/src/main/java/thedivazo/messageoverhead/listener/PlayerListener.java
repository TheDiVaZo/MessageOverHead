package thedivazo.messageoverhead.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.bubble.BubbleManager;
import thedivazo.messageoverhead.bubble.BubbleSpawned;

public class PlayerListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        BubbleManager bubbleManager = MessageOverHead.getConfigManager().getBubbleManager();
        Player player = event.getPlayer();
        bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::show);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        BubbleManager bubbleManager = MessageOverHead.getConfigManager().getBubbleManager();
        Player player = event.getPlayer();
        bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::hide);
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        BubbleManager bubbleManager = MessageOverHead.getConfigManager().getBubbleManager();
        Player player = event.getPlayer();
        bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::hide);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        BubbleManager bubbleManager = MessageOverHead.getConfigManager().getBubbleManager();
        Player player = event.getEntity();
        bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::hide);
    }
}
