package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.render.capability.CapabilityContainer;

public interface ComponentContext {
    ActiveBubble bubble();
    CapabilityContainer capabilityContainer();
}
