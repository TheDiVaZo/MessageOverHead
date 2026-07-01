package me.thedivazo.messageoverhead.core.render.capability;

import me.thedivazo.messageoverhead.core.Viewer;

public interface ViewCapability {
    void show(Viewer player);
    void hide(Viewer player);
    void update(Viewer player);
}
