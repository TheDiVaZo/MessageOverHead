package me.thedivazo.messageoverhead.profile;

import me.thedivazo.messageoverhead.core.BubbleFactory;
import me.thedivazo.messageoverhead.core.component.BubbleComponentFactory;
import me.thedivazo.messageoverhead.core.component.ComponentKey;

import java.util.Map;

public interface BubbleProfile {
    ProfileId id();

    BubbleFactory bubbleFactory();

    Map<ComponentKey<?>, BubbleComponentFactory<?>> componentFactories();
}
