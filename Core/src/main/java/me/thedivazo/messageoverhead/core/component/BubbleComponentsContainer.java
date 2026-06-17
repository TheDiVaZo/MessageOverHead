package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.tick.StopReason;
import me.thedivazo.messageoverhead.core.tick.TickResult;
import me.thedivazo.messageoverhead.core.tick.TickableObject;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BubbleComponentsContainer implements TickableObject, ComponentProvider {
    private final ActiveBubble activeBubble;
    private final Map<Class<?>, TickableObject> components = new HashMap<>();
    private boolean hasStopped = false;

    public BubbleComponentsContainer(ActiveBubble activeBubble) {
        this.activeBubble = activeBubble;
    }

    @Override
    public <T> @Nullable T getComponent(Class<T> clazz) {
        Object component = components.get(clazz);
        if (clazz.isInstance(component)) {
            return clazz.cast(component);
        }
        return null;
    }

    @Override
    public <T> boolean hasComponent(Class<T> type) {
        return components.containsKey(type);
    }

    @Override
    public <T extends TickableObject> void setComponent(Class<T> clazz, T component) {
        components.put(clazz, component);
    }

    @Override
    public void tick() {
        if (activeBubble.isRemove() || hasStopped) return;
        components.values().forEach(TickableObject::tick);
    }

    @Override
    public void onTickStart() {
        if (hasStopped) return;
        components.values().forEach(TickableObject::onTickStart);
    }

    @Override
    public void onTickEnd(StopReason stopReason) {
        if (hasStopped) return;
        components.values().forEach(bubble -> bubble.onTickEnd(stopReason));
        components.clear();
        hasStopped = true;
    }
}
