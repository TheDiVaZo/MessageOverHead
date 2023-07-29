package thedivazo.messageoverhead.listener.vanish;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.bubble.BubbleManager;
import thedivazo.messageoverhead.bubble.BubbleSpawned;
import thedivazo.messageoverhead.vanish.DefaultVanishManager;
import thedivazo.messageoverhead.vanish.VanishManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@EqualsAndHashCode(callSuper = false)
public class DefaultVanishListener extends AbstractVanishListener {

    private static final Map<Player, BukkitTask> invisiblePlayers = new HashMap<>();

    public DefaultVanishListener() {
        super(new DefaultVanishManager());
    }

    @Override
    public Predicate<Player> getShowablePredicate() {
        return BubbleManager.getStatusEnabledPredicate().and(BubbleManager.getVanishManagers().visibleForAll(List.of(vanishManager), false));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGameModeChangeEvent(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        GameMode newGameMode = event.getNewGameMode();
        BubbleManager bubbleManager = MessageOverHead.getConfigManager().getBubbleManager();
        if (newGameMode.equals(GameMode.SPECTATOR))
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::hide);
        else if (getShowablePredicate().test(player))
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::show);

    }

    @EventHandler
    public void onEntityPotionEffect(EntityPotionEffectEvent event) {
        if (!event.getEntityType().equals(EntityType.PLAYER)) return;
        Player player = (Player) event.getEntity();
        PotionEffect potionEffect = event.getNewEffect();
        if (potionEffect == null) return;
        BubbleManager bubbleManager = MessageOverHead.getConfigManager().getBubbleManager();
        if (potionEffect.getType().equals(PotionEffectType.INVISIBILITY)) {
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::hide);
            BukkitTask currentBukkitTask = Bukkit.getScheduler().runTaskLaterAsynchronously(
                    MessageOverHead.getInstance(),
                    ()-> {
                        if (super.getShowablePredicate().test(player))
                            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::show);
                    }, potionEffect.getDuration()+5L);
            Optional.ofNullable(invisiblePlayers.get(player)).ifPresent(BukkitTask::cancel);
            invisiblePlayers.put(player, currentBukkitTask);
        }
    }


}
