package thedivazo.messageoverhead.listener.vanish;

import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.bubble.BubbleManager;
import thedivazo.messageoverhead.bubble.BubbleSpawned;

import java.util.Optional;

public class SuperVanishListener implements Listener {

    @EventHandler
    private void onVanishStateChange(PlayerVanishStateChangeEvent event) {
        Player player = Bukkit.getPlayer(event.getUUID());
        if (player == null) return;
        BubbleManager bubbleManager = MessageOverHead.getConfigManager().getBubbleManager();
        if (event.isVanishing())
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::hide);
        else
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::show);
    }
}
