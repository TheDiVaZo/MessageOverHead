package thedivazo.messageoverhead.listener.vanish;

import lombok.EqualsAndHashCode;
import net.ess3.api.events.VanishStatusChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.bubble.BubbleManager;
import thedivazo.messageoverhead.bubble.BubbleSpawned;
import thedivazo.messageoverhead.vanish.EssentialsXVanishManager;

@EqualsAndHashCode(callSuper = false)
public class EssentialsXVanishListener extends AbstractVanishListener {

    public EssentialsXVanishListener() {
        super(new EssentialsXVanishManager());
    }

    @EventHandler
    public void onVanish(VanishStatusChangeEvent event) {
        Player player = event.getAffected().getBase();
        BubbleManager bubbleManager = MessageOverHead.getConfigManager().getBubbleManager();
        if (event.getValue())
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::hide);
        else if (getShowablePredicate().test(player))
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::show);
    }
}
