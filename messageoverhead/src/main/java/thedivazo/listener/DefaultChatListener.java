package thedivazo.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import thedivazo.MessageOverHear;

public class DefaultChatListener implements Listener, ChatListener<AsyncPlayerChatEvent, PlayerCommandPreprocessEvent> {

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        MessageOverHear.getBubbleMessageManager().removeBubble(e.getPlayer().getUniqueId());
    }

    @Override
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        MessageOverHear.createBubbleMessage(e.getPlayer(), e.getMessage());
    }

    @Override
    @EventHandler
    public void onPlayerPrivateChat(PlayerCommandPreprocessEvent e) {
        String cmd = e.getMessage().trim();
        String[] args = cmd.split(" ");
        if(args.length < 3) return;
        if(!args[0].equals("/msg") && !args[0].equals("/tell")) return;
        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if(targetPlayer==null) return;
        Player player = e.getPlayer();
        String message = args[2];

        MessageOverHear.createBubbleMessage(player, message, targetPlayer);

    }
}
