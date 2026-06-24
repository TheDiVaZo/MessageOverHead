package me.thedivazo.messageoverhead.profile;

import com.google.common.collect.Maps;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import me.thedivazo.messageoverhead.core.BubbleFactory;
import me.thedivazo.messageoverhead.core.component.BubbleComponentFactory;
import me.thedivazo.messageoverhead.core.component.ComponentKey;
import me.thedivazo.messageoverhead.core.component.ProfileComponent;

import java.util.Map;

public interface BubbleProfile {
    ProfileId id();

    BubbleFactory bubbleFactory();

    Map<ComponentKey<?>, BubbleComponentFactory<?>> componentFactories();

    static BubbleProfile create(
            ProfileId id,
            BubbleFactory bubbleFactory,
            Map<ComponentKey<?>, BubbleComponentFactory<?>> componentFactories
    ) {
        return new BubbleProfileImpl(
                id,
                bubbleFactory,
                componentFactories
        );
    }
}
