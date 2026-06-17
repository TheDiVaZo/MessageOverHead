package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.render.capability.RendererView;
import me.thedivazo.messageoverhead.core.tick.StopReason;
import me.thedivazo.messageoverhead.util.Positionc;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ScopeComponentView implements ScopeComponent<ScopeComponentView.ViewPredicate> {
    private final Settings settings;
    private final double viewRadiusSquared;

    private final ActiveBubble activeBubble;
    private final RendererView rendererView;
    private final List<ComponentScoped<ViewPredicate>> components = new ArrayList<>();
    private final List<Player> visiblePlayers = new ArrayList<>();
    private final List<Player> visiblePlayersView = Collections.unmodifiableList(visiblePlayers);

    private final ViewPredicate cachedViewPredicate = new ViewPredicate();

    public ScopeComponentView(ActiveBubble activeBubble, RendererView rendererView, Settings settings) {
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

    @Override
    public boolean add(ComponentScoped<ViewPredicate> componentScoped) {
        return components.add(componentScoped);
    }

    @Override
    public void tick() {
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
    public void onTickEnd(StopReason stopReason) {
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
        cachedViewPredicate.setPlayer(player);
        cachedViewPredicate.setVisible(true);

        for (ComponentScoped<ViewPredicate> component : components) {
            component.apply(cachedViewPredicate, activeBubble);
            if (!cachedViewPredicate.isVisible()) {
                return false;
            }
        }
        return true;
    }

    public static final class Settings {
        private final double viewRadius;
        private final int updateIntervalTicks;

        public Settings(double viewRadius) {
            this(viewRadius, 5);
        }

        public Settings(double viewRadius, int updateIntervalTicks) {
            if (viewRadius < 0) {
                throw new IllegalArgumentException("viewRadius must be non-negative");
            }
            if (updateIntervalTicks < 1) {
                throw new IllegalArgumentException("updateIntervalTicks must be at least 1");
            }

            this.viewRadius = viewRadius;
            this.updateIntervalTicks = updateIntervalTicks;
        }

        public double viewRadius() {
            return viewRadius;
        }

        public int updateIntervalTicks() {
            return updateIntervalTicks;
        }
    }

    public static final class ViewPredicate {
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
}
