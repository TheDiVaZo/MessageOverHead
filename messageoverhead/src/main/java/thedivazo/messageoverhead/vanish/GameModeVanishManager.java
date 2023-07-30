package thedivazo.messageoverhead.vanish;

import lombok.EqualsAndHashCode;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@EqualsAndHashCode(callSuper = false)
public class GameModeVanishManager implements VanishManager {
    @Override
    public boolean isInvisible(Player viewer) {
        return viewer.getGameMode().equals(GameMode.SPECTATOR);
    }
}
