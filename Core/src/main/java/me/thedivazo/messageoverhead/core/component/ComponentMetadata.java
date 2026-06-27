package me.thedivazo.messageoverhead.core.component;

import java.util.Set;

public record ComponentMetadata(
        Set<Class<?>> requiredCapabilities,
        TypeComponent typeComponent
) {
    public static final ComponentMetadata EMPTY = new ComponentMetadata();
    private ComponentMetadata() {
        this(Set.of(), TypeComponent.DEFAULT);
    }

    public ComponentMetadata(TypeComponent typeComponent) {
        this(Set.of(), typeComponent);
    }

    public ComponentMetadata(Set<Class<?>> requiredCapabilities) {
        this(requiredCapabilities, TypeComponent.DEFAULT);
    }
}
