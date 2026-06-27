package me.thedivazo.messageoverhead.core.component;

public interface BubbleComponent {
    default void onAttached(){};
    default void onPostInit(){};

    default void onDetached(){};

    default void onTick(){};
}
