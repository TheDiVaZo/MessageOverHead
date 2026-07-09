package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.annotation.MainThread;
import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.Viewer;
import me.thedivazo.messageoverhead.core.component.scope.BubbleScopeComponent;
import me.thedivazo.messageoverhead.core.component.scope.ComponentScoped;
import me.thedivazo.messageoverhead.core.component.scope.ScopedFactory;
import me.thedivazo.messageoverhead.core.render.capability.ViewCapability;
import me.thedivazo.messageoverhead.util.Positionc;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

@MainThread
public class ViewComponent implements BubbleScopeComponent<ViewComponent.ViewState> {
    private static final ComponentKey<ViewComponent> KEY = new ComponentKey<>(
            ComponentId.of("messageoverhead", "view"),
            ViewComponent.class,
            new ComponentMetadata(
                    Set.of(ViewCapability.class),
                    TypeComponent.VIEW
            )
    );

    private final Settings settings;
    private final double viewRadiusSquared;

    private final Supplier<? extends Iterable<Viewer>> allPlayerProvider;
    private final ActiveBubble activeBubble;
    private final ViewCapability viewCapability;
    private final Map<String, ComponentScoped<ViewState>> components = new LinkedHashMap<>();

    private final ViewState cachedViewState = new ViewState();

    private ViewComponent(Supplier<? extends Iterable<Viewer>> allPlayerProvider, ActiveBubble activeBubble, ViewCapability viewCapability, Settings settings) {
        this.allPlayerProvider = Objects.requireNonNull(allPlayerProvider, "allPlayerProvider");
        this.settings = Objects.requireNonNull(settings, "settings");
        this.viewRadiusSquared = settings.viewRadius() * settings.viewRadius();
        this.activeBubble = Objects.requireNonNull(activeBubble, "activeBubble");
        this.viewCapability = Objects.requireNonNull(viewCapability, "rendererView");
    }

    public Settings settings() {
        return settings;
    }

    public double viewRadius() {
        return settings.viewRadius();
    }

    public int updateIntervalTicks() {
        return settings.updateIntervalTicks();
    }

    public Collection<Viewer> visiblePlayers() {
        return viewCapability.viewers();
    }

    public boolean isVisible(Viewer player) {
        return viewCapability.viewers().contains(player);
    }

    @Override
    public @Nullable ComponentScoped<ViewState> attach(String id, ScopedFactory<ViewState> scopedFactory) {
        Objects.requireNonNull(scopedFactory, "scopedFactory");
        String scopedId = requireScopedId(id);
        try {
            return addScoped(scopedId, createScoped(scopedId, scopedFactory));
        } catch (Exception | Error exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public @Nullable ComponentScoped<ViewState> detach(String id) {
        ComponentScoped<ViewState> detached = components.remove(requireScopedId(id));
        if (detached != null) {
            detached.onDetached();
        }
        return detached;
    }

    @Override
    public @Nullable ComponentScoped<ViewState> get(String id) {
        return components.get(requireScopedId(id));
    }

    @Override
    public boolean contains(String id) {
        return components.containsKey(requireScopedId(id));
    }

    private ComponentScoped<ViewState> addScoped(String id, ComponentScoped<ViewState> componentScoped) {
        String scopedId = requireScopedId(id);
        Objects.requireNonNull(componentScoped, "componentScoped");
        ComponentScoped<ViewState> previous = components.get(scopedId);
        if (previous == componentScoped) {
            return componentScoped;
        }
        componentScoped.onAttached(activeBubble);
        components.put(scopedId, componentScoped);
        if (previous != null) {
            previous.onDetached();
        }
        return componentScoped;
    }

    private ComponentScoped<ViewState> createScoped(String id, ScopedFactory<ViewState> scopedFactory) throws Exception {
        ComponentScoped<ViewState> componentScoped = scopedFactory.create(activeBubble);
        if (componentScoped == null) {
            throw new IllegalStateException("Scoped factory returned null for " + id);
        }
        return componentScoped;
    }

    private static String requireScopedId(String id) {
        return Objects.requireNonNull(id, "id");
    }

    @Override
    public void onTick() {
        if (activeBubble.ageTicks() % settings.updateIntervalTicks() != 0) {
            viewCapability.updateAll();
            return;
        }
        List<Viewer> nextVisiblePlayers = new ArrayList<>();
        Positionc bubblePosition = activeBubble.author().getPosition();

        for (Viewer player : allPlayerProvider.get()) {
            if (!isInsideViewRadius(player, bubblePosition) || !isAllowedByComponents(player)) {
                continue;
            }

            nextVisiblePlayers.add(player);
            if (!viewCapability.viewers().contains(player)) {
                viewCapability.show(player);
            }
        }

        List<Viewer> viewers = List.copyOf(viewCapability.viewers());
        for (int i = 0; i < viewers.size(); i++) {
            Viewer player = viewers.get(i);
            if (!nextVisiblePlayers.contains(player)) {
                viewCapability.hide(player);
            }
        }

        viewCapability.updateAll();
    }

    @Override
    public void onDetached() {
        List<Viewer> viewers = List.copyOf(viewCapability.viewers());
        for (int i = 0; i < viewers.size(); i++) {
            Viewer player = viewers.get(i);
            viewCapability.hide(player);
        }
        detachAllScoped();
    }

    private void detachAllScoped() {
        for (ComponentScoped<ViewState> component : components.values()) {
            try {
                component.onDetached();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        components.clear();
    }

    private boolean isInsideViewRadius(Viewer player, Positionc bubblePosition) {
        Positionc playerLocation = player.getPosition();
        if (!Objects.equals(player.getWorldUID(), activeBubble.author().getWorldUID())) return false;
        double offsetX = playerLocation.x() - bubblePosition.x();
        double offsetY = playerLocation.y() - bubblePosition.y();
        double offsetZ = playerLocation.z() - bubblePosition.z();
        return offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ <= viewRadiusSquared;
    }

    private boolean isAllowedByComponents(Viewer player) {
        cachedViewState.setPlayer(player);
        cachedViewState.setVisible(true);

        for (ComponentScoped<ViewState> component : components.values()) {
            component.onTick(cachedViewState);
            if (!cachedViewState.isVisible()) {
                return false;
            }
        }
        return true;
    }

    public record Settings(double viewRadius, int updateIntervalTicks) {
            public Settings(double viewRadius) {
                this(viewRadius, 5);
            }

            public Settings {
                if (viewRadius < 0) {
                    throw new IllegalArgumentException("viewRadius must be non-negative");
                }
                if (updateIntervalTicks < 1) {
                    throw new IllegalArgumentException("updateIntervalTicks must be at least 1");
                }

            }
        }

    public static final class ViewState {
        private Viewer player;
        private boolean visible = true;

        public Viewer getPlayer() {
            return player;
        }

        private void setPlayer(Viewer player) {
            this.player = player;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }
    }

    public static ComponentKey<ViewComponent> key() {
        return KEY;
    }

    public static Factory factory(Supplier<? extends Iterable<Viewer>> allPlayerProvider, Settings settings) {
        return new Factory(allPlayerProvider, settings, Map.of());
    }

    public static Factory factory(
            Supplier<? extends Iterable<Viewer>> allPlayerProvider,
            Settings settings,
            Map<String, ScopedFactory<ViewState>> scopedFactories
    ) {
        return new Factory(allPlayerProvider, settings, scopedFactories);
    }

    public static ViewComponent attach(ActiveBubble activeBubble, Factory factory) {
        return activeBubble.container().attach(key(), factory);
    }

    public static @Nullable ViewComponent detach(ActiveBubble activeBubble) {
        return activeBubble.container().detach(key());
    }

    public static @Nullable ViewComponent get(ActiveBubble activeBubble) {
        return activeBubble.container().get(key());
    }

    public static boolean contains(ActiveBubble activeBubble) {
        return activeBubble.container().contains(key());
    }

    public static final class Factory implements BubbleComponentFactory<ViewComponent> {
        private final Supplier<? extends Iterable<Viewer>> allPlayerProvider;
        private final Settings settings;
        private final Map<String, ScopedFactory<ViewState>> scopedFactories;

        private Factory(
                Supplier<? extends Iterable<Viewer>> allPlayerProvider,
                Settings settings,
                Map<String, ScopedFactory<ViewState>> scopedFactories
        ) {
            this.allPlayerProvider = Objects.requireNonNull(allPlayerProvider, "allPlayerProvider");
            this.settings = Objects.requireNonNull(settings, "settings");
            Objects.requireNonNull(scopedFactories, "scopedFactories");
            Map<String, ScopedFactory<ViewState>> scopedFactoriesCopy = new LinkedHashMap<>();
            for (Map.Entry<String, ScopedFactory<ViewState>> entry : scopedFactories.entrySet()) {
                scopedFactoriesCopy.put(
                        requireScopedId(entry.getKey()),
                        Objects.requireNonNull(entry.getValue(), "scopedFactory")
                );
            }
            this.scopedFactories = Collections.unmodifiableMap(scopedFactoriesCopy);
        }

        @Override
        public ViewComponent create(ActiveBubble bubble) throws Exception {
            ViewComponent component = new ViewComponent(allPlayerProvider, bubble, bubble.capabilities().requireCapability(ViewCapability.class), settings);
            for (Map.Entry<String, ScopedFactory<ViewState>> entry : scopedFactories.entrySet()) {
                component.addScoped(entry.getKey(), component.createScoped(entry.getKey(), entry.getValue()));
            }
            return component;
        }
    }
}
