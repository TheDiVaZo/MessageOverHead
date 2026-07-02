package me.thedivazo.messageoverhead.vanish;

import org.bukkit.entity.Player;

public final class SuperVanishManager extends VanishManager {
    public static final String SCOPED_ID = "super-vanish";
    private static final String VANISH_API_CLASS = "de.myzelyam.api.vanish.VanishAPI";

    @Override
    public boolean canSee(Player viewer, Player viewed) {
        return canSeeBySuperVanish(viewer, viewed);
    }

    @Override
    public boolean isInvisible(Player player) {
        return isInvisibleBySuperVanish(player);
    }

    private boolean canSeeBySuperVanish(Player viewer, Player viewed) {
        try {
            Class<?> vanishApi = Class.forName(VANISH_API_CLASS);
            return Boolean.TRUE.equals(vanishApi.getMethod("canSee", Player.class, Player.class).invoke(null, viewer, viewed));
        } catch (ReflectiveOperationException | LinkageError exception) {
            return true;
        }
    }

    private boolean isInvisibleBySuperVanish(Player player) {
        try {
            Class<?> vanishApi = Class.forName(VANISH_API_CLASS);
            return Boolean.TRUE.equals(vanishApi.getMethod("isInvisible", Player.class).invoke(null, player));
        } catch (ReflectiveOperationException | LinkageError exception) {
            return false;
        }
    }
}
