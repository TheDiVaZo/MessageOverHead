package me.thedivazo.messageoverhead.core.render.capability;

import me.thedivazo.messageoverhead.core.Viewer;

import java.util.Collection;
import java.util.List;

public interface ViewCapability {
    void show(Viewer player);
    void hide(Viewer player);
    void updateAll();
    Collection<Viewer> viewers();
}
