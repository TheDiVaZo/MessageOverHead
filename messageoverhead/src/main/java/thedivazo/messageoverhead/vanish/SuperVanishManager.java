package thedivazo.messageoverhead.vanish;

import de.myzelyam.api.vanish.VanishAPI;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
@EqualsAndHashCode(callSuper = false)
public final class SuperVanishManager implements VanishManager {

    @Override
    public boolean isInvisible(Player player) {
        return VanishAPI.isInvisible(player);
    }
}
