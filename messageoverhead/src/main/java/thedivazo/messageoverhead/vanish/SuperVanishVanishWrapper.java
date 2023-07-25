package thedivazo.messageoverhead.vanish;

import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.entity.Player;

public final class SuperVanishVanishWrapper implements VanishWrapper {
    @Override
    public boolean canSee(Player viewer, Player viewed) {
        return VanishAPI.canSee(viewer, viewed);
    }

    @Override
    public boolean isInvisible(Player player) {
        return VanishAPI.isInvisible(player);
    }
}
