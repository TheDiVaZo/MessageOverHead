package thedivazo.bubblemessagemanager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import thedivazo.bubble.BubbleMessage;
import thedivazo.MessageOverHead;
import thedivazo.config.ConfigBubble;
import thedivazo.config.ConfigManager;
import thedivazo.utils.TextManager;
import thedivazo.utils.SymbolManager;

import javax.annotation.Nullable;
import java.util.*;

public abstract class BubbleMessageManager<T> {

    protected ConfigManager configManager = MessageOverHead.getConfigManager();
    private final HashMap<T, BubbleMessage<T>> bubbleMessageMap = new HashMap<>();
    private JavaPlugin plugin;

    protected BubbleMessageManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    protected void addBubble(T uuid, BubbleMessage bubbleMessage) {
        bubbleMessageMap.put(uuid, bubbleMessage);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public Map<T, BubbleMessage<T>> getBubbleMessageMap() {
        return Collections.unmodifiableMap(bubbleMessageMap);
    }

    public void removeBubble(T object) {
        if (bubbleMessageMap.containsKey(object)) {
            bubbleMessageMap.get(object).remove();
            bubbleMessageMap.remove(object);
        }
    }

    public void removeBubble(BubbleMessage<T> bubbleMessage) {
        removeBubble(bubbleMessage.getBubbleID());
    }

    public void showBubble(BubbleMessage<T> bubbleMessage, Player showPlayer, Player placeholderPlayer) {
        if (showPlayer.equals(bubbleMessage.getBubbleID()) && !bubbleMessage.getConfigBubble().isVisibleTextForOwner()) return;
        if (bubbleMessage.getConfigBubble().isSoundEnable())
            bubbleMessage.playSound(showPlayer);
        if (bubbleMessage.getConfigBubble().isParticleEnable())
            bubbleMessage.playParticle(showPlayer);
        bubbleMessage.show(showPlayer, placeholderPlayer);
    }

    public void showBubble(BubbleMessage<T> bubbleMessage, Set<Player> showPlayers) {
        showPlayers.forEach((Player player)-> showBubble(bubbleMessage, player, player));
    }

    public void showBubble(BubbleMessage<T> bubbleMessage, Set<Player> showPlayers, Player placeholderPlayer) {
        showPlayers.forEach((Player player)-> showBubble(bubbleMessage, player, placeholderPlayer));
    }
    public void removeAllBubbles() {
        bubbleMessageMap.values().forEach(BubbleMessage::remove);
        bubbleMessageMap.clear();
    }

    public List<String> generateTextFromFormat(ConfigBubble configBubble,@Nullable Player formatter, String message) {
        List<String> messageLines = new ArrayList<>();
        List<String> formatLines = configBubble.getFormatOfPlayer(formatter);
        for(String format: formatLines) {
            if(format != null) {
                messageLines.addAll(TextManager.ofText(convertMsgToLinesBubble(configBubble,format.replace("%message%", configManager.isClearColorFromMessage() ? TextManager.toNoColorString(message):message))));
            }
        }
        return messageLines;
    }

    public abstract Location getLocation(T object);

    public BubbleMessage<T> generateBubbleMessage(@Nullable Player formatter, ConfigBubble configBubble, T object, String message) {
        if(Objects.isNull(configManager)) configManager = MessageOverHead.getConfigManager();
        List<String> messageLines = generateTextFromFormat(configBubble, formatter, message);
        removeBubble(object);

        Location loc = getLocation(object);
        loc.setY(loc.getY() + configBubble.getBiasY());

        BubbleMessage<T> bubbleMessage = new BubbleMessage<>(object, loc, messageLines, configBubble);

        addBubble(object, bubbleMessage);

        BukkitRunnable bukkitRunnableTaskBehavior = getBukkitBehaviorTask(object, configBubble, bubbleMessage);
        BukkitRunnable bukkitRemoveTask = getBukkitRemoveTask(bukkitRunnableTaskBehavior, object);

        bubbleMessage.setTask(
                bukkitRemoveTask.runTaskLaterAsynchronously(getPlugin(), configBubble.getDelay() * 20L),
                bukkitRunnableTaskBehavior.runTaskTimerAsynchronously(getPlugin(), 1L, 1L)
        );

        return bubbleMessage;
    }

    public abstract BukkitRunnable getBukkitBehaviorTask(T object, ConfigBubble configBubble, BubbleMessage<T> bubbleMessage);

    public BukkitRunnable getBukkitRemoveTask(BukkitRunnable bukkitTaskBehavior, T object) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                bukkitTaskBehavior.cancel();
                removeBubble(object);
            }
        };
    }

    public List<String> convertMsgToLinesBubble(ConfigBubble configBubble, String msg) {
        msg = TextManager.ofText(msg);
        return SymbolManager.insertsSymbol(SymbolManager.stripsSymbol(msg, TextManager.CHAT_COLOR_PAT), TextManager.splitText(ChatColor.stripColor(msg), configBubble.getSizeLine()));
    }
}
