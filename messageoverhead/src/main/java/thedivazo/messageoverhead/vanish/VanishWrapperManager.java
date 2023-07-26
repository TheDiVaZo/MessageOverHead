package thedivazo.messageoverhead.vanish;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

public class VanishWrapperManager implements VanishWrapper {
    private final VanishWrapper DEFAULT_VANISH_WRAPPER = new DefaultVanishWrapper();
    private List<VanishWrapper> vanishWrappers = new ArrayList<>();
    public void loadVanish() {
        vanishWrappers.clear();
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.isPluginEnabled("CMI")) {
            vanishWrappers.add(new CMIVanishWrapper());
        }
        if (pluginManager.isPluginEnabled("SuperVanish")) {
            vanishWrappers.add(new SuperVanishVanishWrapper());
        }
    }


    @Override
    public boolean canSee(Player viewer, Player viewed) {
        return vanishWrappers.stream()
                .map(vanishWrapper -> vanishWrapper.canSee(viewer, viewed))
                .reduce((a,b)->a || b).orElse(true) || DEFAULT_VANISH_WRAPPER.canSee(viewer, viewed);
    }

    @Override
    public boolean isInvisible(Player player) {
        return vanishWrappers.stream()
                .map(vanishWrapper -> vanishWrapper.isInvisible(player))
                .reduce((a,b)->a || b).orElse(true) || DEFAULT_VANISH_WRAPPER.isInvisible(player);
    }
}
