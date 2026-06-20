package me.thedivazo.messageoverhead.command;

import me.thedivazo.messageoverhead.core.AuthorBubble;
import me.thedivazo.messageoverhead.util.Position;
import me.thedivazo.messageoverhead.util.Positionc;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

final class PlayerBubbleAuthor implements AuthorBubble {
    private final Player player;
    private final Positionc positionc;

    PlayerBubbleAuthor(Player player) {
        this.player = Objects.requireNonNull(player, "player");

        positionc = new Positionc() {
            private final Location cachedLocation = player.getLocation();

            @Override
            public double x() {
                return player.getLocation(cachedLocation).getX();
            }

            @Override
            public double y() {
                return player.getLocation(cachedLocation).getY();
            }

            @Override
            public double z() {
                return player.getLocation(cachedLocation).getZ();
            }
        };
    }

    @Override
    public UUID getUID() {
        return player.getUniqueId();
    }

    @Override
    public boolean isLive() {
        return player.isOnline();
    }

    @Override
    public Positionc getPosition() {
        return positionc;
    }

    @Override
    public UUID getWorldUID() {
        return player.getWorld().getUID();
    }

}
