package thedivazo.messageoverhead.vanish;

import de.myzelyam.api.vanish.VanishAPI;
import de.myzelyam.supervanish.SuperVanish;
import org.bukkit.entity.Player;

public final class SuperVanishManager implements VanishManager {
    @Override
    public boolean canSee(Player viewer, Player viewed) {
        return VanishAPI.canSee(viewer, viewed);
    }

    @Override
    public boolean isInvisible(Player player) {
        return VanishAPI.isInvisible(player);
    }
}
