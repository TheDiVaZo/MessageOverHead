package thedivazo.messageoverhead.bubble;

import lombok.EqualsAndHashCode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

public interface Bubble {
    void show(Set<Player> showers);
    void setPosition(Location loc, Set<Player> showers);
    void hide(Set<Player> showers);

    boolean equals(Object o);

    int hashCode();
}
