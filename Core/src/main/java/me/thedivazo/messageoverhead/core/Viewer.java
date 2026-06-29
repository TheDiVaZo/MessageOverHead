package me.thedivazo.messageoverhead.core;

import me.thedivazo.messageoverhead.annotation.MainThread;
import org.bukkit.entity.Player;

@MainThread
public interface Viewer extends Author {

    Player getPlayer();
}
