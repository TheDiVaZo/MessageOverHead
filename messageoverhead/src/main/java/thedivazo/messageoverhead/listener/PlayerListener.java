package thedivazo.messageoverhead.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

public class PlayerListener implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        //MessageOverHead.getBubbleMessageManager().removeBubble(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        //MessageOverHead.getBubbleMessageManager().removeBubble(e.getEntity());
    }

    @EventHandler
    public void onInvisible(EntityPotionEffectEvent e) {
        if(!e.getModifiedType().equals(PotionEffectType.INVISIBILITY)) return;
        if(!(e.getEntity() instanceof Player)) return;
       //MessageOverHead.getBubbleMessageManager().removeBubble((Player) e.getEntity());
    }
}
