package me.thedivazo.messageoverhead.armorstand;

import me.thedivazo.messageoverhead.util.Position;
import me.thedivazo.messageoverhead.util.Positionc;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;

public class GroupedFakeArmorStand implements ArmorStand {
    private final List<ArmorStand> stands;
    private final Set<Player> visiblePlayers = Collections.newSetFromMap(new WeakHashMap<>());
    private final double lineSpacing;
    private final Position position;
    private boolean destroyed;

    public GroupedFakeArmorStand(List<ArmorStand> stands, double lineSpacing, Positionc position) {
        this.stands = List.copyOf(Objects.requireNonNull(stands, "stands"));
        this.lineSpacing = lineSpacing;
        this.position = new Position(Objects.requireNonNull(position, "position"));
        setPosition(position.x(), position.y(), position.z());
    }

    public int countLines() {
        return stands.size();
    }

    public double getLineSpacing() {
        return lineSpacing;
    }

    @Override
    public void setPosition(double x, double y, double z) {
        if (destroyed) {
            return;
        }

        position.set(x, y, z);

        double offset = lineSpacing * stands.size();
        for (int i = 0; i < stands.size(); i++) {
            stands.get(i).setPosition(x, y + offset, z);
            offset -= lineSpacing;
        }
    }

    @Override
    public void show(Player player) {
        Objects.requireNonNull(player, "player");
        if (destroyed || !visiblePlayers.add(player)) {
            return;
        }

        List<ArmorStand> shownStands = new ArrayList<>(stands.size());
        try {
            for (ArmorStand stand : stands) {
                stand.show(player);
                shownStands.add(stand);
            }
        } catch (RuntimeException | Error exception) {
            visiblePlayers.remove(player);
            hideShownStands(player, shownStands, exception);
            throw exception;
        }
    }

    @Override
    public void updatePosition(Player player) {
        Objects.requireNonNull(player, "player");
        if (destroyed || !visiblePlayers.contains(player)) {
            return;
        }

        stands.forEach(stand -> stand.updatePosition(player));
    }

    @Override
    public void hide(Player player) {
        Objects.requireNonNull(player, "player");
        if (destroyed || !visiblePlayers.remove(player)) {
            return;
        }

        try {
            stands.forEach(stand -> stand.hide(player));
        } catch (RuntimeException | Error exception) {
            visiblePlayers.add(player);
            throw exception;
        }
    }

    @Override
    public void destroy() {
        if (destroyed) {
            return;
        }

        for (Player player : List.copyOf(visiblePlayers)) {
            hide(player);
        }

        destroyed = true;
        visiblePlayers.clear();
        stands.forEach(ArmorStand::destroy);
    }

    private static void hideShownStands(
            Player player,
            List<ArmorStand> shownStands,
            Throwable originalException
    ) {
        for (ArmorStand shownStand : shownStands) {
            try {
                shownStand.hide(player);
            } catch (RuntimeException | Error rollbackException) {
                originalException.addSuppressed(rollbackException);
            }
        }
    }
}
