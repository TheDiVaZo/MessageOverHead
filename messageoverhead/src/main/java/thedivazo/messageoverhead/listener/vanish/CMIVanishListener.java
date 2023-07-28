package thedivazo.messageoverhead.listener.vanish;

import com.Zrips.CMI.events.CMIPlayerUnVanishEvent;
import com.Zrips.CMI.events.CMIPlayerVanishEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.bubble.BubbleManager;
import thedivazo.messageoverhead.bubble.BubbleSpawned;

public class CMIVanishListener implements Listener {

    @EventHandler
    private void onVanish(CMIPlayerVanishEvent event) {
        Player player = event.getPlayer();
        BubbleManager bubbleManager = MessageOverHead.getConfigManager().getBubbleManager();
        bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::hide);
    }

    @EventHandler
    private void onUnVanish(CMIPlayerUnVanishEvent event) {
        Player player = event.getPlayer();
        BubbleManager bubbleManager = MessageOverHead.getConfigManager().getBubbleManager();
        bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::show);
    }
}
