package me.thedivazo.messageoverhead.core.component;

public interface BubbleComponent {
    default TypeComponent getType(){return TypeComponent.DEFAULT;};
    default void onAttached(){};
    default void onPostInit(){};

    default void onDetached(){};

    default void onTick(){};

}
