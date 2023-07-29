package thedivazo.messageoverhead.listener.vanish;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import thedivazo.messageoverhead.bubble.BubbleManager;
import thedivazo.messageoverhead.vanish.VanishManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@AllArgsConstructor
public abstract class AbstractVanishListener implements Listener {
    protected VanishManager vanishManager;

    public Predicate<Player> getShowablePredicate() {
        return BubbleManager.getStatusEnabledPredicate().and(BubbleManager.getNoSpectatorPredicate())
                .and(BubbleManager.getVanishManagers().visibleForAll(List.of(vanishManager), false));
    }
}
