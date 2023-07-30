package thedivazo.messageoverhead.vanish;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
@EqualsAndHashCode(callSuper = false)
public class EssentialsXVanishManager implements VanishManager{

    @Override
    public boolean isInvisible(Player viewer) {
        Essentials essentials = JavaPlugin.getPlugin(Essentials.class);
        return essentials.getUser(viewer).isVanished();
    }
}
