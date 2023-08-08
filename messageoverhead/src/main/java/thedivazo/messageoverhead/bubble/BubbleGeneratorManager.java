package thedivazo.messageoverhead.bubble;

import lombok.Builder;
import lombok.Singular;
import org.bukkit.entity.Player;
import thedivazo.messageoverhead.channel.Channel;

import java.util.*;

public class BubbleGeneratorManager {
    private final Set<BubbleGenerator> bubbles;

    @Builder
    public BubbleGeneratorManager(@Singular Set<BubbleGenerator> bubbles) {
        this.bubbles = bubbles;
    }

    public Optional<BubbleGenerator> getBubbleGenerator(Player sender, Channel channel) {
        return bubbles.stream()
                .filter(bubbleGenerator->bubbleGenerator.hasPlayerBubbleModelPermission(sender) && bubbleGenerator.isChannel(channel))
                .findFirst();
    }

    public Optional<BubbleGenerator> getBubbleGenerator(String name) {
        return bubbles.stream()
                .filter(bubbleGenerator -> bubbleGenerator.getName().equals(name))
                .findFirst();
    }
}
