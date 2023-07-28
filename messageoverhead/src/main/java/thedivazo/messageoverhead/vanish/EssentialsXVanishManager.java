package thedivazo.messageoverhead.vanish;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EssentialsXVanishManager implements VanishManager{

    @Override
    public boolean canSee(Player viewer, Player viewed) {
        Essentials essentials = JavaPlugin.getPlugin(Essentials.class);
        return essentials.getUser(viewer).isVanished();
    }

    @Override
    public boolean isInvisible(Player player) {
        Essentials essentials = JavaPlugin.getPlugin(Essentials.class);
        return essentials.getUser(player).isVanished();
    }
}
