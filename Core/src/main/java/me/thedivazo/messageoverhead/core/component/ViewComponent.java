package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.MessageOverHeadPlugin;
import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.render.capability.RendererView;
import me.thedivazo.messageoverhead.util.Positionc;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class ViewComponent extends BubbleComponent {
    private final Settings settings;
    private final double viewRadiusSquared;

    private final ActiveBubble activeBubble;
    private final RendererView rendererView;
    private final List<BiConsumer<ViewState, ActiveBubble>> components = new ArrayList<>();
    private final List<Player> visiblePlayers = new ArrayList<>();
    private final List<Player> visiblePlayersView = Collections.unmodifiableList(visiblePlayers);

    private final ViewState cachedViewState = new ViewState();

    private ViewComponent(ActiveBubble activeBubble, RendererView rendererView, Settings settings) {
        this.settings = Objects.requireNonNull(settings, "settings");
        this.viewRadiusSquared = settings.viewRadius() * settings.viewRadius();
        this.activeBubble = activeBubble;
        this.rendererView = rendererView;
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

    public List<Player> visiblePlayers() {
        return visiblePlayersView;
    }

    public boolean isVisible(Player player) {
        return visiblePlayers.contains(player);
    }

    public boolean add(BiConsumer<ViewState, ActiveBubble> componentScoped) {
        return components.add(componentScoped);
    }

    @Override
    public void onTick() {
        if (activeBubble.ageTicks() % settings.updateIntervalTicks() != 0) {
            return;
        }

        List<Player> nextVisiblePlayers = new ArrayList<>();
        Positionc bubblePosition = activeBubble.author().getPosition();

        for (Player player : Bukkit.getOnlinePlayers()) {
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

        for (Player player : visiblePlayers) {
            if (!nextVisiblePlayers.contains(player)) {
                rendererView.hide(player);
            }
        }

        visiblePlayers.clear();
        visiblePlayers.addAll(nextVisiblePlayers);
    }

    @Override
    public void onDetached() {
        for (Player player : visiblePlayers) {
            rendererView.hide(player);
        }
        visiblePlayers.clear();
    }

    private boolean isInsideViewRadius(Player player, Positionc bubblePosition) {
        Location playerLocation = player.getLocation();
        if (!Objects.equals(playerLocation.getWorld().getUID(), activeBubble.author().getWorldUID())) return false;
        double offsetX = playerLocation.getX() - bubblePosition.x();
        double offsetY = playerLocation.getY() - bubblePosition.y();
        double offsetZ = playerLocation.getZ() - bubblePosition.z();
        return offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ <= viewRadiusSquared;
    }

    private boolean isAllowedByComponents(Player player) {
        cachedViewState.setPlayer(player);
        cachedViewState.setVisible(true);

        for (BiConsumer<ViewState, ActiveBubble> component : components) {
            component.accept(cachedViewState, activeBubble);
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
        private Player player;
        private boolean visible = true;

        public Player getPlayer() {
            return player;
        }

        private void setPlayer(Player player) {
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
        return MessageOverHeadPlugin.getInstance().getComponentService().VIEW;
    }

    public static Factory factory(Settings settings) {
        return new Factory(settings);
    }

    public static @Nullable ViewComponent attach(ActiveBubble activeBubble, Factory factory) {
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
        private final Settings settings;

        private Factory(Settings settings) {
            this.settings = settings;
        }

        @Override
        public boolean isAttachable(ComponentContext context) {
            return context.capabilityContainer().capabilityOrNull(RendererView.class) != null;
        }

        @Override
        public ViewComponent create(ComponentContext context) throws Exception {
            return new ViewComponent(context.bubble(), context.capabilityContainer().requireCapability(RendererView.class), settings);
        }
    }
}
