package me.thedivazo.messageoverhead.spigot.bubble;

import me.thedivazo.messageoverhead.common.bubble.Bubble;
import me.thedivazo.messageoverhead.common.message.Message;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface LocateBubble<T extends Message<?>, K> extends Bubble<T, K> {
    Location getLocation();
}