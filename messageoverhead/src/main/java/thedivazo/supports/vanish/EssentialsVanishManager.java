package thedivazo.supports.vanish;

import com.earth2me.essentials.Essentials;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EssentialsVanishManager extends DefaultVanishManager {
    @Override
    public boolean canSee(Player viewer, Player viewed) {
        return super.canSee(viewer, viewed) && !JavaPlugin.getPlugin(Essentials.class).getUser(viewed).isVanished();
    }

    @Override
    public boolean isInvisible(Player player) {
        return super.isInvisible(player) && !JavaPlugin.getPlugin(Essentials.class).getUser(player).isVanished();
    }
}
