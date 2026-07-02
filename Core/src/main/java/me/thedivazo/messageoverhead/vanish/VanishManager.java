package me.thedivazo.messageoverhead.vanish;

import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.Viewer;
import me.thedivazo.messageoverhead.core.component.ViewComponent;
import me.thedivazo.messageoverhead.core.component.scope.ComponentScoped;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

public abstract class VanishManager implements ComponentScoped<ViewComponent.ViewState> {
    private ActiveBubble activeBubble;

    @Override
    public void onAttached(ActiveBubble activeBubble) {
        this.activeBubble = Objects.requireNonNull(activeBubble, "activeBubble");
    }

    @Override
    public void onDetached() {
        activeBubble = null;
    }

    @Override
    public void onTick(ViewComponent.ViewState context) {
        Objects.requireNonNull(context, "context");
        if (!context.isVisible() || activeBubble == null) {
            return;
        }

        Viewer viewer = context.getPlayer();
        if (viewer == null) {
            return;
        }

        Player viewed = Bukkit.getPlayer(activeBubble.author().getUID());
        if (viewed == null) {
            return;
        }

        if (!canSee(viewer.getPlayer(), viewed)) {
            context.setVisible(false);
        }
    }

    public abstract boolean canSee(Player viewer, Player viewed);

    public abstract boolean isInvisible(Player player);
}
