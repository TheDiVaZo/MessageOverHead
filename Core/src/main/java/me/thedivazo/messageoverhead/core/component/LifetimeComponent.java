package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.core.ActiveBubble;
import org.jetbrains.annotations.Nullable;

public class LifetimeComponent implements BubbleComponent {
    private static final ComponentKey<LifetimeComponent> KEY = new ComponentKey<>(
            ComponentId.of("messageoverhead", "lifetime"),
            LifetimeComponent.class,
            new ComponentMetadata(TypeComponent.BUBBLE)
    );

    private final ActiveBubble activeBubble;
    private final long lifetime;

    public LifetimeComponent(ActiveBubble activeBubble, long lifetime) {
        this.activeBubble = activeBubble;
        this.lifetime = lifetime;
    }

    @Override
    public void onTick() {
        if (activeBubble.ageTicks() > lifetime) {
            activeBubble.remove();
        }
    }

    public static ComponentKey<LifetimeComponent> key() {
        return KEY;
    }

    public static Factory factory(long lifetime) {
        return new Factory(lifetime);
    }

    public static LifetimeComponent attach(ActiveBubble activeBubble, long lifetime) {
        return activeBubble.container().attach(key(), factory(lifetime));
    }

    public static @Nullable LifetimeComponent detach(ActiveBubble activeBubble) {
        return activeBubble.container().detach(key());
    }

    public static @Nullable LifetimeComponent get(ActiveBubble activeBubble) {
        return activeBubble.container().get(key());
    }

    public static boolean contains(ActiveBubble activeBubble) {
        return activeBubble.container().contains(key());
    }

    public static final class Factory implements BubbleComponentFactory<LifetimeComponent> {
        private final long lifetime;

        public Factory(long lifetime) {
            this.lifetime = lifetime;
        }

        @Override
        public LifetimeComponent create(ComponentContext context) {
            return new LifetimeComponent(context.bubble(), lifetime);
        }
    }
}
