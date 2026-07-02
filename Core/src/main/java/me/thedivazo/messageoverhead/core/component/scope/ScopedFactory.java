package me.thedivazo.messageoverhead.core.component.scope;

import me.thedivazo.messageoverhead.core.ActiveBubble;

public interface ScopedFactory<C> {
    ComponentScoped<C> create(ActiveBubble bubble) throws Exception;
}
