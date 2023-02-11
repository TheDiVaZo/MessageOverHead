package thedivazo.listener.chatlistener;

import api.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import thedivazo.MessageOverHear;

public final class DefaultChatListener implements Listener, ChatListener<AsyncPlayerChatEvent, PlayerCommandPreprocessEvent> {

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
        args[0] = "";
        args[1] = "";
        String message = String.join(" ", args);

        MessageOverHear.createBubbleMessage(MessageOverHear.getConfigManager().getConfigBubble("messages"), player, message, targetPlayer);
    }

    @Override
    public void disableListener() {
        PlayerCommandPreprocessEvent.getHandlerList().unregister(MessageOverHear.getInstance());
        AsyncPlayerChatEvent.getHandlerList().unregister(MessageOverHear.getInstance());
    }
}
