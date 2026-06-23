package me.thedivazo.messageoverhead.profile;

import me.thedivazo.messageoverhead.core.BubbleFactory;
import me.thedivazo.messageoverhead.core.component.BubbleComponent;
import me.thedivazo.messageoverhead.core.component.BubbleComponentFactory;
import me.thedivazo.messageoverhead.core.component.ComponentKey;

import java.util.*;

public interface BubbleProfile {
    UUID id();
    BubbleFactory bubbleFactory();
    Set<KeyToFactoryEntry<?>> componentFactories();

    record KeyToFactoryEntry<T extends BubbleComponent>(ComponentKey<T> key, BubbleComponentFactory<T> factory) {
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            KeyToFactoryEntry<?> that = (KeyToFactoryEntry<?>) o;
            return Objects.equals(key, that.key);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key);
        }
    }
}
