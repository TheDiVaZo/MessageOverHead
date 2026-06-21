package me.thedivazo.messageoverhead.core.component;

public interface BubbleComponent {
    default void onAttached(){};

    default void onDetached(){};

    void onTick();

}
