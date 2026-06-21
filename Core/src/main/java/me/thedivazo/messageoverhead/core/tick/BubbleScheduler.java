package me.thedivazo.messageoverhead.core.tick;

import me.thedivazo.messageoverhead.core.ActiveBubble;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface BubbleScheduler extends AutoCloseable {
    @Nullable ActiveBubble put(SchedulableBubble bubble);

    @Nullable ActiveBubble get(UUID uid);

    @Nullable ActiveBubble remove(UUID uid);

    boolean contains(UUID uid);

    void clear();

    @Override
    void close();
}
