package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.annotation.MainThread;
import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.render.capability.CapabilityContainer;

@MainThread
public interface ComponentContext {
    ActiveBubble bubble();
    CapabilityContainer capabilityContainer();
}
