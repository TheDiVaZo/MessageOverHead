package thedivazo.supports.vanish;

import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.entity.Player;

public final class SuperVansihManager extends DefaultVanishManager{
    @Override
    public boolean canSee(Player viewer, Player viewed) {
        return super.canSee(viewer, viewed) && VanishAPI.canSee(viewer, viewed);
    }

    @Override
    public boolean isInvisible(Player player) {
        return super.isInvisible(player) && VanishAPI.isInvisible(player);
    }
}
