package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.core.ActiveBubble;

public interface ComponentScoped<A> {
    boolean apply(A outputValue, ActiveBubble bubble);
}
