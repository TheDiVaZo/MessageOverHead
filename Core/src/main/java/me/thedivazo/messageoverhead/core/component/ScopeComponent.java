package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.core.tick.TickableObject;

public interface ScopeComponent<A> extends TickableObject {
    boolean add(ComponentScoped<A> componentScoped);
}
