package thedivazo.messageoverhead.vanish;

import com.Zrips.CMI.CMI;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;

@EqualsAndHashCode(callSuper = false)
public class CMIVanishManager implements VanishManager {

    @Override
    public boolean isInvisible(Player viewer) {
        return !CMI.getInstance().getPlayerManager().getUser(viewer).isVanished();
    }
}
