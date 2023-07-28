package thedivazo.messageoverhead.listener.vanish;

import net.ess3.api.events.VanishStatusChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.bubble.BubbleManager;
import thedivazo.messageoverhead.bubble.BubbleSpawned;

public class EssentialsXVanishListener implements Listener {

    @EventHandler
    public void onVanish(VanishStatusChangeEvent event) {
        Player player = event.getAffected().getBase();
        BubbleManager bubbleManager = MessageOverHead.getConfigManager().getBubbleManager();
        if (event.getValue())
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::hide);
        else
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::show);
    }
}
