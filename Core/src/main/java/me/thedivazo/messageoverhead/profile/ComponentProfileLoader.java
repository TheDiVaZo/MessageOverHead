package me.thedivazo.messageoverhead.profile;

import me.thedivazo.messageoverhead.core.component.BubbleComponent;
import me.thedivazo.messageoverhead.core.component.BubbleComponentFactory;
import me.thedivazo.messageoverhead.core.component.ComponentKey;

public interface ComponentProfileLoader<T extends BubbleComponent, C> {
    ComponentKey<T> key();

    BubbleComponentFactory<T> load(C context);
}
