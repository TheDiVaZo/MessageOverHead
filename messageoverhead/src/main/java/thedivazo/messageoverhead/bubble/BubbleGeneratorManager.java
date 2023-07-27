package thedivazo.messageoverhead.bubble;

import lombok.Builder;
import lombok.Singular;
import org.bukkit.entity.Player;
import thedivazo.messageoverhead.bubble.exception.BubbleModelNotFoundException;
import thedivazo.messageoverhead.channel.Channel;

import java.util.*;

public class BubbleGeneratorManager {
    private final Set<BubbleGenerator> bubbles;

    @Builder
    public BubbleGeneratorManager(@Singular Set<BubbleGenerator> bubbles) {
        this.bubbles = bubbles;
    }

    public BubbleGenerator getBubbleGenerator(Player sender, Channel channel) throws BubbleModelNotFoundException {
        Optional<BubbleGenerator> bubbleGeneratorOptional = bubbles.stream().filter(bubbleGenerator->bubbleGenerator.isPermission(sender) && bubbleGenerator.isChannel(channel)).findFirst();
        if (bubbleGeneratorOptional.isEmpty()) throw new BubbleModelNotFoundException("bubble model for player "+sender.getName()+" not found");
        return bubbleGeneratorOptional.get();
    }
}
