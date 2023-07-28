package thedivazo.messageoverhead.bubble;

import thedivazo.messageoverhead.logging.Logger;
import lombok.Getter;
import org.bukkit.entity.Player;
import thedivazo.messageoverhead.bubble.exception.BubbleModelNotFoundException;
import thedivazo.messageoverhead.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BubbleManager {
    private final Map<Player, BubbleSpawned> bubbles = new HashMap<>();

    public Optional<BubbleSpawned> getBubbleSpawned(Player player) {
        return Optional.ofNullable(bubbles.get(player));
    }



    @Getter
    private final BubbleGeneratorManager bubbleGeneratorManager;

    public BubbleManager(BubbleGeneratorManager bubbleGeneratorManager) {
        this.bubbleGeneratorManager = bubbleGeneratorManager;
    }

    public void spawnBubble(String playerText, BubbleGenerator bubbleGenerator, Player sender, Set<Player> showers) {
        bubbles.put(sender,bubbleGenerator.spawnBubble(playerText, sender, showers));
    }

    public void spawnBubble(String playerText, Channel channel, Player sender, Set<Player> showers) {
        if (bubbles.containsKey(sender)) bubbles.get(sender).remove();
        bubbleGeneratorManager
                .getBubbleGenerator(sender, channel)
                .ifPresentOrElse(
                        bubbleGenerator -> spawnBubble(playerText, bubbleGenerator, sender, showers),
                        ()->Logger.debug("no bubble model found for player '{0}' (UUID: '{1}' )", sender.getName(), sender.getUniqueId().toString()));
    }

    public void removeAllBubbles() {
        bubbles.values().forEach(BubbleSpawned::remove);
        bubbles.clear();
    }
}
