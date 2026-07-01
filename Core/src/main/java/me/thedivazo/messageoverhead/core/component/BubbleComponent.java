package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.annotation.MainThread;

@MainThread
public interface BubbleComponent {
    default void onPreAttached(){};
    default void onPostInit(){};

    default void onDetached(){};

    default void onTick(){};
}
