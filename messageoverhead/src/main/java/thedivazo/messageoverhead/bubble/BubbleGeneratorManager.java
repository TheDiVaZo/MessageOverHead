package thedivazo.messageoverhead.bubble;

import lombok.Builder;
import lombok.Singular;
import org.bukkit.entity.Player;
import thedivazo.messageoverhead.bubble.exception.BubbleModelNotFoundException;
import thedivazo.messageoverhead.config.channel.Channel;
import thedivazo.messageoverhead.utils.text.DecoratedStringUtils;

import java.util.*;

public class BubbleGeneratorManager {
    private final Set<BubbleGenerator> bubbles;

    @Builder
    public BubbleGeneratorManager(@Singular Set<BubbleGenerator> bubbles) {
        this.bubbles = bubbles;
    }

    public BubbleSpawned spawnBubble(String playerText, Channel channel, Player ownerBubble, Set<Player> showers) throws BubbleModelNotFoundException {
        Optional<BubbleGenerator> bubbleGeneratorOptional = bubbles.stream().filter(bubbleGenerator->bubbleGenerator.isPermission(ownerBubble) && bubbleGenerator.isChannel(channel)).findFirst();
        if (bubbleGeneratorOptional.isEmpty()) throw new BubbleModelNotFoundException("bubble model for player "+ownerBubble.getName()+" not found");
        BubbleGenerator bubbleGenerator = bubbleGeneratorOptional.get();
        BubbleWrapper bubbleWrapper = bubbleGenerator.generateBubble(DecoratedStringUtils.wrapString(playerText), ownerBubble);
        return new BubbleSpawned(bubbleWrapper, ownerBubble, showers);
    }
}
