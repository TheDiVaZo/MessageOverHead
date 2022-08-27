package thedivazo.bubblemessagemanager;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import thedivazo.BubbleMessage;
import thedivazo.MessageOverHear;
import thedivazo.config.ConfigManager;
import thedivazo.utils.StringColorUtils;
import thedivazo.utils.StringUtil;

import java.util.*;

import static thedivazo.config.ConfigManager.getFormatOfPlayer;


public final class DefaultBubbleMessageManager extends BubbleMessageManager {

    ConfigManager configManager = MessageOverHear.getConfigManager();

    public DefaultBubbleMessageManager() {
        super(MessageOverHear.getInstance());
    }

    public List<String> convertMsgToLinesBubble(String msg) {
        msg = StringColorUtils.ofText(msg);
        return StringUtil.insertsSymbol(StringUtil.stripsSymbol(msg, StringColorUtils.CHAT_COLOR_PAT), StringUtil.splitText(ChatColor.stripColor(msg), configManager.getSizeLine()));
    }

    public BubbleMessage generateBubbleMessage(Player player, String message) {
        List<String> messageLines = new ArrayList<>();
        List<String> formatLines = getFormatOfPlayer(player);
        for(String format: formatLines) {
            if(format != null) {
                messageLines.addAll(StringColorUtils.ofText(convertMsgToLinesBubble(StringUtil.setEmoji(player, StringUtil.setPlaceholders(player, format).replace("%message%", StringColorUtils.toNoColorString(message))))));
            }
        }

        removeBubble(player.getUniqueId());

        Location loc = player.getLocation();
        loc.setY(loc.getY() + configManager.getBiasY());

        BubbleMessage bubbleMessage = new BubbleMessage(player, loc, messageLines);

        addBubble(player.getUniqueId(), bubbleMessage);

        BukkitTask taskMove = new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = player.getLocation().clone();
                loc.setY(loc.getY() + configManager.getBiasY());
                bubbleMessage.setPosition(loc);
            }
        }.runTaskTimerAsynchronously(getPlugin(), 1L, 1L);

        BukkitTask taskDelete = new BukkitRunnable() {
            @Override
            public void run() {
                taskMove.cancel();
                removeBubble(bubbleMessage);
            }
        }.runTaskLaterAsynchronously(getPlugin(), configManager.getDelay() * 20L);
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
        if (showPlayer.equals(ownerPlayer) && !configManager.isVisibleTextForOwner()) return;
        if (configManager.isSoundEnable())
            bubbleMessage.playSound(showPlayer);
        if (configManager.isParticleEnable())
            bubbleMessage.playParticle(showPlayer);
        bubbleMessage.show(showPlayer);
    }
}
