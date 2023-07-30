package thedivazo.messageoverhead.vanish;

import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import thedivazo.messageoverhead.BubbleActiveStatus;

@EqualsAndHashCode(callSuper = false)
public class BubbleActiveStatusVanishManager implements VanishManager {
    @Override
    public boolean isInvisible(Player viewer) {
        return BubbleActiveStatus.getStatus(viewer).equals(BubbleActiveStatus.Status.DISABLED);
    }
}
