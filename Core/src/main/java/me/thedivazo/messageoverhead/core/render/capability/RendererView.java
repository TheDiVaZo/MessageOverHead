package me.thedivazo.messageoverhead.core.render.capability;

import org.bukkit.entity.Player;

public interface RendererView {
    void show(Player player);
    void hide(Player player);
    void update(Player player);
}
