package me.thedivazo.messageoverhead.core;

import me.thedivazo.messageoverhead.util.Positionc;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class OnlinePlayerProvider implements Supplier<Iterable<Viewer>> {
    private final Map<UUID, OnlinePlayerViewer> viewersByUid = new HashMap<>();

    @Override
    public Iterable<Viewer> get() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        List<Viewer> viewers = new ArrayList<>(onlinePlayers.size());

        for (Player player : onlinePlayers) {
            OnlinePlayerViewer viewer = viewersByUid.computeIfAbsent(
                    player.getUniqueId(),
                    OnlinePlayerViewer::new
            );
            viewer.setPlayer(player);
            viewers.add(viewer);
        }

        return viewers;
    }

    private static final class OnlinePlayerViewer implements Viewer {
        private final UUID uid;
        private final Positionc position;

        private Player player;

        private OnlinePlayerViewer(UUID uid) {
            this.uid = Objects.requireNonNull(uid, "uid");
            this.position = new Positionc() {
                private final Location cachedLocation = new Location(null, 0, 0, 0);

                @Override
                public double x() {
                    return getPlayer().getLocation(cachedLocation).getX();
                }

                @Override
                public double y() {
                    return getPlayer().getLocation(cachedLocation).getY();
                }

                @Override
                public double z() {
                    return getPlayer().getLocation(cachedLocation).getZ();
                }
            };
        }

        private void setPlayer(Player player) {
            this.player = Objects.requireNonNull(player, "player");
        }

        @Override
        public Player getPlayer() {
            if (player == null) {
                throw new IllegalStateException("Player " + uid + " has not been resolved yet");
            }
            return player;
        }

        @Override
        public UUID getUID() {
            return uid;
        }

        @Override
        public boolean isLive() {
            return player != null && player.isOnline();
        }

        @Override
        public Positionc getPosition() {
            return position;
        }

        @Override
        public UUID getWorldUID() {
            return getPlayer().getWorld().getUID();
        }
    }
}
