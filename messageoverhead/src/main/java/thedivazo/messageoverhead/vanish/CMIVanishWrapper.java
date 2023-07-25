package thedivazo.messageoverhead.vanish;

import com.Zrips.CMI.CMI;
import org.bukkit.entity.Player;

public class CMIVanishWrapper implements VanishWrapper {
    @Override
    public boolean canSee(Player viewer, Player viewed) {
        return !CMI.getInstance().getPlayerManager().getUser(viewed).isVanished();
    }

    @Override
    public boolean isInvisible(Player player) {
        return !CMI.getInstance().getPlayerManager().getUser(player).isVanished();
    }
}
