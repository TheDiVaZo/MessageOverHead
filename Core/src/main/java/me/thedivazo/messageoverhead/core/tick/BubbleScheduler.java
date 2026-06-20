package me.thedivazo.messageoverhead.core.tick;

import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.TickableActiveBubble;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface BubbleScheduler {
    @Nullable ActiveBubble put(TickableActiveBubble tickable);

    @Nullable ActiveBubble get(UUID uid);

    @Nullable ActiveBubble remove(UUID uid);

    boolean contains(UUID uid);
}
