package thedivazo.messageoverhead.listener.vanish;

import com.Zrips.CMI.events.CMIPlayerUnVanishEvent;
import com.Zrips.CMI.events.CMIPlayerVanishEvent;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.bubble.BubbleManager;
import thedivazo.messageoverhead.bubble.BubbleSpawned;
import thedivazo.messageoverhead.vanish.CMIVanishManager;
import thedivazo.messageoverhead.vanish.VanishManager;

import java.util.List;
import java.util.function.Predicate;

@EqualsAndHashCode(callSuper = false)
public class CMIVanishListener implements VanishListener {

    VanishManager vanishManager = new CMIVanishManager();
    Predicate<Player> showable = BubbleManager.getVanishManagers().visibleForAll(List.of(vanishManager), false);


    @EventHandler
    public void onVanish(CMIPlayerVanishEvent event) {
        Player player = event.getPlayer();
        BubbleManager bubbleManager = MessageOverHead.getConfigManager().getBubbleManager();
        bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::hide);
    }

    @EventHandler
    public void onUnVanish(CMIPlayerUnVanishEvent event) {
        Player player = event.getPlayer();
        BubbleManager bubbleManager = MessageOverHead.getConfigManager().getBubbleManager();
        if (showable.test(player))
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::show);
    }
}
