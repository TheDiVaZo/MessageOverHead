package thedivazo.manager.vanish;

import org.bukkit.entity.Player;

public interface VanishManager {
    boolean canSee(Player viewer, Player viewed);
    boolean isInvisible(Player player);
}
