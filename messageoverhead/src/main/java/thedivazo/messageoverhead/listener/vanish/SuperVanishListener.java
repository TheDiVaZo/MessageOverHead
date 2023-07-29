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

@EqualsAndHashCode(callSuper = false)
public class SuperVanishListener extends AbstractVanishListener {

    public SuperVanishListener() {
        super(new SuperVanishManager());
    }

    @EventHandler
    public void onVanishStateChange(PlayerVanishStateChangeEvent event) {
        Player player = Bukkit.getPlayer(event.getUUID());
        if (player == null) return;
        BubbleManager bubbleManager = MessageOverHead.getConfigManager().getBubbleManager();
        if (event.isVanishing())
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::hide);
        else if(getShowablePredicate().test(player))
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::show);
    }
}
