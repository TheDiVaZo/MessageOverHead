package thedivazo.messageoverhead.listener.chatlistener;

import de.jeter.chatex.api.events.PlayerUsesGlobalChatEvent;
import de.jeter.chatex.api.events.PlayerUsesRangeModeEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.channel.ChannelFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class ChatExListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    private void onChat(PlayerUsesGlobalChatEvent chatEvent) {
        Player sender = chatEvent.getPlayer();
        String message = chatEvent.getMessage();
        Set<Player> recipients = Bukkit.getOnlinePlayers().stream().map(OfflinePlayer::getPlayer).collect(Collectors.toSet());
        MessageOverHead.getConfigManager().getBubbleManager().spawnBubble(message, ChannelFactory.create("all"), sender, recipients);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onChat(PlayerUsesRangeModeEvent chatEvent) {
        Player sender = chatEvent.getPlayer();
        String message = chatEvent.getMessage();
        Set<Player> recipients = Bukkit.getOnlinePlayers().stream().map(OfflinePlayer::getPlayer).collect(Collectors.toSet());
        MessageOverHead.getConfigManager().getBubbleManager().spawnBubble(message, ChannelFactory.create("all"), sender, recipients);
    }
}
