package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.ComponentService;
import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.Viewer;
import me.thedivazo.messageoverhead.core.component.scope.BubbleScopeComponent;
import me.thedivazo.messageoverhead.core.component.scope.ComponentScoped;
import me.thedivazo.messageoverhead.core.render.capability.RendererView;
import me.thedivazo.messageoverhead.util.Positionc;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class ViewComponent implements BubbleScopeComponent<ViewComponent.ViewState> {
    private static final ComponentKey<ViewComponent> KEY = new ComponentKey<>(
            ComponentId.of("messageoverhead", "view"),
            ViewComponent.class,
            new ComponentMetadata(
                    Set.of(RendererView.class)
            )
    );

    private final Settings settings;
    private final double viewRadiusSquared;

    private final Supplier<? extends Iterable<Viewer>> allPlayerProvider;
    private final ActiveBubble activeBubble;
    private final RendererView rendererView;
    private final List<ComponentScoped<ViewState>> components = new ArrayList<>();
    private final List<Viewer> visiblePlayers = new ArrayList<>();
    private final List<Viewer> visiblePlayersView = Collections.unmodifiableList(visiblePlayers);

    private final ViewState cachedViewState = new ViewState();

    private ViewComponent(Supplier<? extends Iterable<Viewer>> allPlayerProvider, ActiveBubble activeBubble, RendererView rendererView, Settings settings) {
        this.allPlayerProvider = Objects.requireNonNull(allPlayerProvider, "allPlayerProvider");
        this.settings = Objects.requireNonNull(settings, "settings");
        this.viewRadiusSquared = settings.viewRadius() * settings.viewRadius();
        this.activeBubble = Objects.requireNonNull(activeBubble, "activeBubble");
        this.rendererView = Objects.requireNonNull(rendererView, "rendererView");
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

    public List<Viewer> visiblePlayers() {
        return visiblePlayersView;
    }

    public boolean isVisible(Viewer player) {
        return visiblePlayers.contains(player);
    }

    @Override
    public void attach(Function<ActiveBubble, ComponentScoped<ViewState>> scopedFactory) {
        Objects.requireNonNull(scopedFactory, "scopedFactory");
        addScoped(Objects.requireNonNull(scopedFactory.apply(activeBubble), "scopedFactory result"));
    }

    private boolean addScoped(ComponentScoped<ViewState> componentScoped) {
        return components.add(Objects.requireNonNull(componentScoped, "componentScoped"));
    }

    @Override
    public void onTick() {
        if (activeBubble.ageTicks() % settings.updateIntervalTicks() != 0) {
            visiblePlayers.forEach(rendererView::update);
            return;
        }

        List<Viewer> nextVisiblePlayers = new ArrayList<>();
        Positionc bubblePosition = activeBubble.author().getPosition();

        for (Viewer player : allPlayerProvider.get()) {
            if (!isInsideViewRadius(player, bubblePosition) || !isAllowedByComponents(player)) {
                continue;
            }

            nextVisiblePlayers.add(player);
            if (visiblePlayers.contains(player)) {
                rendererView.update(player);
            } else {
                rendererView.show(player);
            }
        }

        for (Viewer player : visiblePlayers) {
            if (!nextVisiblePlayers.contains(player)) {
                rendererView.hide(player);
            }
        }

        visiblePlayers.clear();
        visiblePlayers.addAll(nextVisiblePlayers);
    }

    @Override
    public void onDetached() {
        for (Viewer player : visiblePlayers) {
            rendererView.hide(player);
        }
        visiblePlayers.clear();
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

        for (ComponentScoped<ViewState> component : components) {
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
        return new Factory(allPlayerProvider, settings);
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

        private Factory(Supplier<? extends Iterable<Viewer>> allPlayerProvider, Settings settings) {
            this.allPlayerProvider = Objects.requireNonNull(allPlayerProvider, "allPlayerProvider");
            this.settings = Objects.requireNonNull(settings, "settings");
        }

        @Override
        public ViewComponent create(ComponentContext context) throws Exception {
            return new ViewComponent(allPlayerProvider, context.bubble(), context.capabilityContainer().requireCapability(RendererView.class), settings);
        }
    }
}
