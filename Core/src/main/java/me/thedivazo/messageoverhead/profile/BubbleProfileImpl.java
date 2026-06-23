package me.thedivazo.messageoverhead.profile;

import me.thedivazo.messageoverhead.core.BubbleFactory;

import java.util.*;

public final class BubbleProfileImpl implements BubbleProfile {
    private final UUID id;
    private final BubbleFactory bubbleFactory;
    private final Set<KeyToFactoryEntry<?>> componentFactories;

    public BubbleProfileImpl(UUID id, BubbleFactory bubbleFactory, Set<KeyToFactoryEntry<?>> componentFactories) {
        this.id = id;
        this.bubbleFactory = bubbleFactory;
        this.componentFactories = Set.copyOf(componentFactories);
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public BubbleFactory bubbleFactory() {
        return bubbleFactory;
    }

    @Override
    public Set<KeyToFactoryEntry<?>> componentFactories() {
        return componentFactories;
    }

}
