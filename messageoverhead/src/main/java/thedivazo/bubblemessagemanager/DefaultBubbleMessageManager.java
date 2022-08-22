package thedivazo.bubblemessagemanager;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import thedivazo.BubbleMessage;
import thedivazo.utils.StringColor;
import thedivazo.utils.StringUtil;

import java.util.*;

import static thedivazo.config.ConfigManager.getFormatOfPlayer;


public class DefaultBubbleMessageManager extends BubbleMessageManager {

    public DefaultBubbleMessageManager(JavaPlugin plugin) {
        super(plugin);
    }

    public List<String> convertMsgToLinesBubble(String msg) {
        msg = StringColor.ofText(msg);
        return StringUtil.insertsSymbol(StringUtil.stripsSymbol(msg, StringColor.CHAT_COLOR_PAT), StringUtil.splitText(ChatColor.stripColor(msg), getSizeLine()));
    }

    public BubbleMessage generateBubbleMessage(Player player, String message) {
        List<String> messageLines = new ArrayList<>();
        List<String> formatLines = getFormatOfPlayer(player);
        for(String format: formatLines) {
            if(format != null) {
                messageLines.addAll(StringColor.ofText(convertMsgToLinesBubble(StringUtil.setEmoji(player, StringUtil.setPlaceholders(player, format).replace("%message%", StringColor.toNoColorString(message))))));
            }
        }

        removeBubble(player.getUniqueId());

        Location loc = player.getLocation();
        loc.setY(loc.getY() + getBiasY());

        BubbleMessage bubbleMessage = new BubbleMessage(player, loc, messageLines);

        addBubble(player.getUniqueId(), bubbleMessage);

        BukkitTask taskMove = new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = player.getLocation().clone();
                loc.setY(loc.getY() + getBiasY());
                bubbleMessage.setPosition(loc);
            }
        }.runTaskTimerAsynchronously(getPlugin(), 1L, 1L);

        BukkitTask taskDelete = new BukkitRunnable() {
            @Override
            public void run() {
                taskMove.cancel();
                removeBubble(bubbleMessage);
            }
        }.runTaskLaterAsynchronously(getPlugin(), getDelay() * 20L);
        bubbleMessage.setTask(taskDelete, taskMove);
        return bubbleMessage;
    }

    @Override
    public void spawnBubble(BubbleMessage bubbleMessage, Set<Player> showPlayers) {
        showPlayers.add(bubbleMessage.getOwnerPlayer());
        showPlayers.forEach((Player player)->spawnBubble(bubbleMessage, player));
    }

    public void spawnBubble(BubbleMessage bubbleMessage, Player showPlayer) {
        Player ownerPlayer = bubbleMessage.getOwnerPlayer();
        if (showPlayer.equals(ownerPlayer) && !isVisibleTextForOwner()) return;
        if (isSoundEnable())
            bubbleMessage.playSound(showPlayer);
        if (isParticleEnable())
            bubbleMessage.playParticle(showPlayer);
        bubbleMessage.show(showPlayer);
    }
}
