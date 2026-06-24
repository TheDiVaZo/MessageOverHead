package me.thedivazo.messageoverhead.core.component;

public enum TypeComponent {

    DEFAULT, //Все остальное
    BUBBLE, //Компоненты, изменяющие внутреннее состояние Bubble (через capability или через ActiveBubble)
    VIEW; //Компоненты, отвечающие за отрисовку
    private final int priority;

    TypeComponent() {
        this.priority = ordinal();
    }

    public int getPriority() {
        return priority;
    }
}
