package thedivazo.bubblemessagemanager;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import thedivazo.BubbleMessage;
import thedivazo.MessageOverHead;
import thedivazo.config.ConfigBubble;



public final class DefaultBubbleMessageManager extends BubbleMessageManager<Player> {

    public DefaultBubbleMessageManager() {
        super(MessageOverHead.getInstance());
    }

    @Override
    public Location getLocation(Player object) {
        return object.getLocation();
    }

    @Override
    public BukkitRunnable getBukkitBehaviorTask(Player object, ConfigBubble configBubble, BubbleMessage<Player> bubbleMessage) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = object.getLocation().clone();
                loc.setY(loc.getY() + configBubble.getBiasY());
                bubbleMessage.setPosition(loc);
            }
        };
    }
}
