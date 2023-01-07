package thedivazo.manager.vanish;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class DefaultVanishManager implements VanishManager{
    @Override
    public boolean canSee(Player viewer, Player viewed) {
        return !viewed.getGameMode().equals(GameMode.SPECTATOR) &&
                !viewed.hasPotionEffect(PotionEffectType.INVISIBILITY) &&
                viewer.canSee(viewed) &&
                viewer.getWorld().equals(viewed.getWorld());
    }

    @Override
    public boolean isInvisible(Player player) {
        return player.getGameMode().equals(GameMode.SPECTATOR) ||
                player.hasPotionEffect(PotionEffectType.INVISIBILITY);
    }

}
