package thedivazo.supports.vanish;

import com.Zrips.CMI.CMI;
import org.bukkit.entity.Player;

public class CMIVanishManager extends DefaultVanishManager{
    @Override
    public boolean canSee(Player viewer, Player viewed) {
        return super.canSee(viewer, viewed) && !CMI.getInstance().getPlayerManager().getUser(viewed).isVanished();
    }

    @Override
    public boolean isInvisible(Player player) {
        return super.isInvisible(player) && !CMI.getInstance().getPlayerManager().getUser(player).isVanished();
    }
}
