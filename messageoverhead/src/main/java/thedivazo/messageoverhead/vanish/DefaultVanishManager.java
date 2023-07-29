package thedivazo.messageoverhead.vanish;

import lombok.EqualsAndHashCode;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
@EqualsAndHashCode(callSuper = false)
public class DefaultVanishManager implements VanishManager {

    @Override
    public boolean isInvisible(Player player) {
        return player.getGameMode().equals(GameMode.SPECTATOR) ||
                player.hasPotionEffect(PotionEffectType.INVISIBILITY);
    }

}
