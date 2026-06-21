package me.thedivazo.messageoverhead.core.component;

import com.google.common.base.Predicates;
import me.thedivazo.messageoverhead.core.ActiveBubble;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

public record ComponentMetadata(
        Set<Class<?>> requiredCapabilities,
        Predicate<ActiveBubble> bubblePredicate
) {
    public static ComponentMetadata EMPTY = new ComponentMetadata();

    public ComponentMetadata() {
        this(Collections.emptySet());
    }

    public ComponentMetadata(Set<Class<?>> requiredCapabilities) {
        this(requiredCapabilities, Predicates.alwaysTrue());
    }

    public ComponentMetadata(Set<Class<?>> requiredCapabilities, Predicate<ActiveBubble> bubblePredicate) {
        this.requiredCapabilities = Set.copyOf(requiredCapabilities);
        this.bubblePredicate = bubblePredicate;
    }
}
