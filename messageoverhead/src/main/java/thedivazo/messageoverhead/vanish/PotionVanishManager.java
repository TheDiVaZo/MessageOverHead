package thedivazo.messageoverhead.vanish;

import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@EqualsAndHashCode(callSuper = false)
public class PotionVanishManager implements VanishManager{
    @Override
    public boolean isInvisible(Player viewer) {
        return viewer.hasPotionEffect(PotionEffectType.INVISIBILITY);
    }
}
