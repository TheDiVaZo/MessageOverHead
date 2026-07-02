package me.thedivazo.messageoverhead.vanish;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class InvisibilityVanishManager extends VanishManager {
    public static final String SCOPED_ID = "invisibility";

    @Override
    public boolean canSee(Player viewer, Player viewed) {
        Objects.requireNonNull(viewer, "viewer");
        Objects.requireNonNull(viewed, "viewed");
        return !isInvisible(viewed)
                && viewer.canSee(viewed)
                && viewer.getWorld().equals(viewed.getWorld());
    }

    @Override
    public boolean isInvisible(Player player) {
        Objects.requireNonNull(player, "player");
        return player.getGameMode().equals(GameMode.SPECTATOR)
                || player.hasPotionEffect(PotionEffectType.INVISIBILITY);
    }
}
