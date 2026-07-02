package me.thedivazo.messageoverhead.core.component.scope;

import me.thedivazo.messageoverhead.core.ActiveBubble;

public interface ComponentScoped<C> {
     default void onAttached(ActiveBubble activeBubble) {}

     default void onDetached() {}

     void onTick(C context);
}
