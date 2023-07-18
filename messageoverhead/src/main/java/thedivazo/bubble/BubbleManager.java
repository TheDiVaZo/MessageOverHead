package thedivazo.bubble;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Singular;
import org.bukkit.entity.Player;
import thedivazo.utils.text.DecoratedString;

import java.util.HashMap;
import java.util.Map;

@Builder
public class BubbleManager {
    @Singular
    private final Map<String, BubbleGenerator> bubbles;

    public BubbleWrapper generateBubble(DecoratedString text, Player ownerBubble) {
        return null;
    }
}
