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

@EqualsAndHashCode(callSuper = false)
public class CMIVanishListener extends AbstractVanishListener {

    public CMIVanishListener() {
        super(new CMIVanishManager());
    }

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
        if (getShowablePredicate().test(player))
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::show);
    }
}
