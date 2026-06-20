package me.thedivazo.messageoverhead.profile;

import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.AuthorBubble;
import me.thedivazo.messageoverhead.core.Message;
import me.thedivazo.messageoverhead.util.Positionc;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface BubbleManager {
    ActiveBubble spawnBubble(Message message, AuthorBubble author, Positionc positionc);

    @Nullable ActiveBubble getBubble(UUID uid);

    boolean containsBubble(UUID uid);
}
