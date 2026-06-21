package me.thedivazo.messageoverhead.core.component.scope;

import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.component.BubbleComponent;

import java.util.function.Function;

public interface BubbleScopeComponent<C> extends BubbleComponent {
    void attach(Function<ActiveBubble, ComponentScoped<C>> scopedFactory);
}
