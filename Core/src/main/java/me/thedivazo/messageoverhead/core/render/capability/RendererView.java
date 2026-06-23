package me.thedivazo.messageoverhead.core.render.capability;

import me.thedivazo.messageoverhead.core.Viewer;

public interface RendererView {
    void show(Viewer player);
    void hide(Viewer player);
    void update(Viewer player);
}
