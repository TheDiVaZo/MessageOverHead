package thedivazo.messageoverhead.listener.vanish;

import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.bubble.BubbleManager;
import thedivazo.messageoverhead.bubble.BubbleSpawned;
import thedivazo.messageoverhead.vanish.SuperVanishManager;
import thedivazo.messageoverhead.vanish.VanishManager;

import java.util.List;
import java.util.function.Predicate;

@EqualsAndHashCode(callSuper = false)
public class SuperVanishListener implements VanishListener {

    private static VanishManager vanishManager = new SuperVanishManager();
    private static Predicate<Player> showable = BubbleManager.getDefaultVanishManagerSet().visibleForAll(List.of(vanishManager), false);

    @EventHandler
    public void onVanishStateChange(PlayerVanishStateChangeEvent event) {
        Player player = Bukkit.getPlayer(event.getUUID());
        if (player == null) return;
        BubbleManager bubbleManager = MessageOverHead.CONFIG_MANAGER.getBubbleManager();
        if (event.isVanishing())
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::hide);
        else if(showable.test(player))
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::show);
    }
}
