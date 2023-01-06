package thedivazo.supports.chatlistener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import thedivazo.MessageOverHear;
import thedivazo.config.ConfigManager;

public final class DefaultChatListener implements Listener, ChatListener<AsyncPlayerChatEvent, PlayerCommandPreprocessEvent> {

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        MessageOverHear.getBubbleMessageManager().removeBubble(e.getPlayer().getUniqueId());
    }

    @Override
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        MessageOverHear.createBubbleMessage(MessageOverHear.getConfigManager().getConfigBubble("messages"), e.getPlayer(), e.getMessage());
    }

    @Override
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPrivateChat(PlayerCommandPreprocessEvent e) {
        String cmd = e.getMessage().trim();
        String[] args = cmd.split(" ");
        if(args.length < 3) return;
        if(!args[0].equals("/msg") && !args[0].equals("/tell")) return;
        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if(targetPlayer==null) return;
        Player player = e.getPlayer();
        String message = args[2];

        MessageOverHear.createBubbleMessage(MessageOverHear.getConfigManager().getConfigBubble("messages"), player, message, targetPlayer);

    }
}
