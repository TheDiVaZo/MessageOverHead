package thedivazo.messageoverhead.listener.vanish;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.bubble.BubbleManager;
import thedivazo.messageoverhead.bubble.BubbleSpawned;

public class DefaultVanishListener implements Listener {

    @EventHandler
    private void onGameModeChangeEvent(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        GameMode newGameMode = event.getNewGameMode();
        BubbleManager bubbleManager = MessageOverHead.getConfigManager().getBubbleManager();
        if (newGameMode.equals(GameMode.SPECTATOR))
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::hide);
        else
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::show);
    }

    @EventHandler
    private void on (EntityPotionEffectEvent event) {
        if (!event.getEntityType().equals(EntityType.PLAYER)) return;
        Player player = (Player) event.getEntity();
        PotionEffect potionEffect = event.getNewEffect();
        if (potionEffect == null) return;
        BubbleManager bubbleManager = MessageOverHead.getConfigManager().getBubbleManager();
        if (potionEffect.getType().equals(PotionEffectType.INVISIBILITY))
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::hide);
        else
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::show);
    }


}
