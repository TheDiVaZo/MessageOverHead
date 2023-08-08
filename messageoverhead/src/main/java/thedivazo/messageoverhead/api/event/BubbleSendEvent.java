package thedivazo.messageoverhead.api.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import thedivazo.messageoverhead.bubble.BubbleSpawned;

@RequiredArgsConstructor
public class BubbleSendEvent extends Event implements Cancellable {
    private static final HandlerList HANDLES = new HandlerList();
    private boolean canceled;

    @Getter
    private final BubbleSpawned bubbleSpawned;

    @Getter
    private final Player sender;

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }

    @NotNull
    public HandlerList getHandlers() {
        return HANDLES;
    }
}
