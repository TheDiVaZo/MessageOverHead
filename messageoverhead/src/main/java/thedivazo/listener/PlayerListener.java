package thedivazo.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import thedivazo.MessageOverHear;

public class PlayerListener implements Listener {
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        MessageOverHear.getBubbleMessageManager().removeBubble(player.getUniqueId());
    }

    public void onDeath(PlayerDeathEvent e) {
        MessageOverHear.getBubbleMessageManager().removeBubble(e.getEntity().getUniqueId());
    }
}
