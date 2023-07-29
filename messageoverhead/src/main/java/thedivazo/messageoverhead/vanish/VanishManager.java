package thedivazo.messageoverhead.vanish;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface VanishManager {
    boolean isInvisible(Player viewer);
}
