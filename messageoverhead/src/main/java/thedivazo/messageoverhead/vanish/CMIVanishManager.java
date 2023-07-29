package thedivazo.messageoverhead.vanish;

import com.Zrips.CMI.CMI;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;

@EqualsAndHashCode(callSuper = false)
public class CMIVanishManager implements VanishManager {

    @Override
    public boolean isInvisible(Player player) {
        return !CMI.getInstance().getPlayerManager().getUser(player).isVanished();
    }
}
