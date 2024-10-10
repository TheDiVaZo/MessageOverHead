package me.thedivazo.messageoverhead.spigot.bubble;

import me.thedivazo.messageoverhead.common.bubble.Bubble;
import me.thedivazo.messageoverhead.common.message.Message;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface EntityLocateBubble<T extends Message<?>, K extends Entity> extends LocateBubble<T, K> {
    @Override
    default Location getLocation() {
        return creator().getLocation();
    }
}
