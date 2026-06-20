package me.thedivazo.messageoverhead.core.component;

public interface BubbleComponentFactory<T extends BubbleComponent> {
    default boolean isAttachable(ComponentContext key) {
        return true;
    }

    T create(ComponentContext context) throws Exception;
}
