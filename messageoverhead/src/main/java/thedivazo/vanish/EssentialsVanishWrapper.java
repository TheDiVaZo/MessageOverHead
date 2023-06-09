package thedivazo.vanish;

import com.earth2me.essentials.Essentials;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EssentialsVanishWrapper implements VanishWrapper {
    @Override
    public boolean canSee(Player viewer, Player viewed) {
        return !JavaPlugin.getPlugin(Essentials.class).getUser(viewed).isVanished();
    }

    @Override
    public boolean isInvisible(Player player) {
        return !JavaPlugin.getPlugin(Essentials.class).getUser(player).isVanished();
    }
}
