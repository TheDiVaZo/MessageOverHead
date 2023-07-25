package thedivazo.messageoverhead.bubble;

import thedivazo.messageoverhead.api.logging.Logger;
import lombok.Getter;
import org.bukkit.entity.Player;
import thedivazo.messageoverhead.bubble.exception.BubbleModelNotFoundException;
import thedivazo.messageoverhead.config.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BubbleManager {
    private final Map<Player, BubbleSpawned> bubbles = new HashMap<>();

    @Getter
    private final BubbleGeneratorManager bubbleGeneratorManager;

    public BubbleManager(BubbleGeneratorManager bubbleGeneratorManager) {
        this.bubbleGeneratorManager = bubbleGeneratorManager;
    }

    public void spawnBubble(String playerText, Channel channel, Player ownerPlayer, Set<Player> showers) {
        if (bubbles.containsKey(ownerPlayer)) bubbles.get(ownerPlayer).remove();
        try {
            bubbles.put(ownerPlayer, bubbleGeneratorManager.spawnBubble(playerText, channel, ownerPlayer, showers));
        } catch (BubbleModelNotFoundException e) {
            Logger.debug(e.getMessage());
        }
    }

    public void removeAllBubbles() {
        bubbles.values().forEach(BubbleSpawned::remove);
        bubbles.clear();
    }
}
