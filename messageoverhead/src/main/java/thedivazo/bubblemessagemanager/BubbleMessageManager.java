package thedivazo.bubblemessagemanager;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import thedivazo.BubbleMessage;

import java.util.*;

public abstract class BubbleMessageManager {
    private final HashMap<UUID, BubbleMessage> bubbleMessageMap = new HashMap<>();
    private int sizeLine = 24;
    private double biasY = 2.15;
    private long delay = 4;
    private boolean isVisibleTextForOwner = true;
    private boolean isSoundEnable = true;
    private boolean isParticleEnable = true;
    private int distance = 10;
    private boolean isShowSpectatorMessage = false;
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

    public int getSizeLine() {
        return sizeLine;
    }

    public void setSizeLine(int sizeLine) {
        this.sizeLine = sizeLine;
    }

    public double getBiasY() {
        return biasY;
    }

    public void setBiasY(double biasY) {
        this.biasY = biasY;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public boolean isVisibleTextForOwner() {
        return isVisibleTextForOwner;
    }

    public void setVisibleTextForOwner(boolean visibleTextForOwner) {
        isVisibleTextForOwner = visibleTextForOwner;
    }

    public boolean isSoundEnable() {
        return isSoundEnable;
    }

    public void setSoundEnable(boolean soundEnable) {
        isSoundEnable = soundEnable;
    }

    public boolean isParticleEnable() {
        return isParticleEnable;
    }

    public void setParticleEnable(boolean particleEnable) {
        isParticleEnable = particleEnable;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public boolean isShowSpectatorMessage() {
        return isShowSpectatorMessage;
    }

    public void setShowSpectatorMessage(boolean showSpectatorMessage) {
        isShowSpectatorMessage = showSpectatorMessage;
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

    public abstract BubbleMessage generateBubbleMessage(Player player, String message);

    public abstract void spawnBubble(BubbleMessage bubbleMessage, Player showPlayer);

    public abstract void spawnBubble(BubbleMessage bubbleMessage, Set<Player> showPlayers);

    public void removeAllBubbles() {
        bubbleMessageMap.values().forEach(BubbleMessage::remove);
        bubbleMessageMap.clear();
    }
}
