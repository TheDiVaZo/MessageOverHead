package thedivazo.messageoverhead.listener.vanish;

import lombok.EqualsAndHashCode;
import net.ess3.api.events.VanishStatusChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.bubble.BubbleManager;
import thedivazo.messageoverhead.bubble.BubbleSpawned;
import thedivazo.messageoverhead.vanish.EssentialsXVanishManager;
import thedivazo.messageoverhead.vanish.VanishManager;

import java.util.List;
import java.util.function.Predicate;

@EqualsAndHashCode(callSuper = false)
public class EssentialsXVanishListener implements VanishListener {

    private static VanishManager vanishManager = new EssentialsXVanishManager();
    private static Predicate<Player> showable = BubbleManager.getDefaultVanishManagerSet().visibleForAll(List.of(vanishManager), false);

    @EventHandler
    public void onVanish(VanishStatusChangeEvent event) {
        Player player = event.getAffected().getBase();
        BubbleManager bubbleManager = MessageOverHead.CONFIG_MANAGER.getBubbleManager();
        if (event.getValue())
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::hide);
        else if (showable.test(player))
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::show);
    }
}
