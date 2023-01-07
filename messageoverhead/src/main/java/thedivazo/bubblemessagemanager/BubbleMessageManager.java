package thedivazo.bubblemessagemanager;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import thedivazo.BubbleMessage;
import thedivazo.config.ConfigBubble;

import java.util.*;

public abstract class BubbleMessageManager {
    private final HashMap<UUID, BubbleMessage> bubbleMessageMap = new HashMap<>();
    private JavaPlugin plugin;

    protected BubbleMessageManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    protected void addBubble(UUID uuid, BubbleMessage bubbleMessage) {
        bubbleMessageMap.put(uuid, bubbleMessage);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public Map<UUID, BubbleMessage> getBubbleMessageMap() {
        return Collections.unmodifiableMap(bubbleMessageMap);
    }

    public void removeBubble(UUID player) {
        if (bubbleMessageMap.containsKey(player)) {
            bubbleMessageMap.get(player).remove();
            bubbleMessageMap.remove(player);
        }
    }

    public void removeBubble(Player player) {
        removeBubble(player.getUniqueId());
    }

    public void removeBubble(BubbleMessage bubbleMessage) {
        removeBubble(bubbleMessage.getOwnerPlayer());
    }

    public abstract BubbleMessage generateBubbleMessage(ConfigBubble configBubble, Player player, String message);

    public abstract void spawnBubble(BubbleMessage bubbleMessage, Player showPlayer);

    public abstract void spawnBubble(BubbleMessage bubbleMessage, Set<Player> showPlayers);

    public void removeAllBubbles() {
        bubbleMessageMap.values().forEach(BubbleMessage::remove);
        bubbleMessageMap.clear();
    }
}
