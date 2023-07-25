package thedivazo.messageoverhead.vanish;

import org.bukkit.entity.Player;

public interface VanishWrapper {
    boolean canSee(Player viewer, Player viewed);
    boolean isInvisible(Player player);
}
