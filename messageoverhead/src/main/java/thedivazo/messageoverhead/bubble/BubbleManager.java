package thedivazo.messageoverhead.bubble;

import thedivazo.messageoverhead.logging.Logger;
import lombok.Getter;
import org.bukkit.entity.Player;
import thedivazo.messageoverhead.bubble.exception.BubbleModelNotFoundException;
import thedivazo.messageoverhead.channel.Channel;

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

    public void spawnBubble(String playerText, Channel channel, Player sender, Set<Player> showers) {
        if (bubbles.containsKey(sender)) bubbles.get(sender).remove();
        try {
            bubbles.put(sender, bubbleGeneratorManager.getBubbleGenerator(sender, channel).spawnBubble(playerText, sender, showers));
        } catch (BubbleModelNotFoundException e) {
            Logger.debug(e.getMessage());
        }
    }

    public void removeAllBubbles() {
        bubbles.values().forEach(BubbleSpawned::remove);
        bubbles.clear();
    }
}
