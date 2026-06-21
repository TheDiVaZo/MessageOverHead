package me.thedivazo.messageoverhead.core.component;

public interface BubbleComponentFactory<T extends BubbleComponent> {
    T create(ComponentContext context) throws Exception;
}
