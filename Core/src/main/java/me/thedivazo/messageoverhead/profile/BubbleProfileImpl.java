package me.thedivazo.messageoverhead.profile;

import me.thedivazo.messageoverhead.core.BubbleFactory;
import me.thedivazo.messageoverhead.core.component.BubbleComponentFactory;
import me.thedivazo.messageoverhead.core.component.ComponentKey;

import java.util.*;

public final class BubbleProfileImpl implements BubbleProfile {
    private final ProfileId id;
    private final BubbleFactory bubbleFactory;
    private final Map<ComponentKey<?>, BubbleComponentFactory<?>> componentFactories;

    public BubbleProfileImpl(
            ProfileId id,
            BubbleFactory bubbleFactory,
            Map<ComponentKey<?>, BubbleComponentFactory<?>> componentFactories
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.bubbleFactory = Objects.requireNonNull(bubbleFactory, "bubbleFactory");
        this.componentFactories = Map.copyOf(Objects.requireNonNull(componentFactories, "componentFactories"));
    }

    @Override
    public ProfileId id() {
        return id;
    }

    @Override
    public BubbleFactory bubbleFactory() {
        return bubbleFactory;
    }

    @Override
    public Map<ComponentKey<?>, BubbleComponentFactory<?>> componentFactories() {
        return componentFactories;
    }

}
