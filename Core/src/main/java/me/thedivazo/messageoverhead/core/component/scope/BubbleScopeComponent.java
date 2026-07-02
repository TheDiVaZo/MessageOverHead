package me.thedivazo.messageoverhead.core.component.scope;

import me.thedivazo.messageoverhead.core.component.BubbleComponent;
import org.jetbrains.annotations.Nullable;

public interface BubbleScopeComponent<C> extends BubbleComponent {
    @Nullable ComponentScoped<C> attach(String id, ScopedFactory<C> scopedFactory);

    @Nullable ComponentScoped<C> detach(String id);

    @Nullable ComponentScoped<C> get(String id);

    boolean contains(String id);
}
