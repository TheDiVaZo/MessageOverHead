package thedivazo.messageoverhead.listener.chatlistener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.config.channel.ChannelFactory;

import java.util.Set;

public class DefaultChatListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    private void onChat(AsyncPlayerChatEvent chatEvent) {
        String message = chatEvent.getMessage();
        Player sender = chatEvent.getPlayer();
        Set<Player> recipients = chatEvent.getRecipients();
        MessageOverHead.getConfigManager().getBubbleManager().spawnBubble(message, ChannelFactory.create("all"), sender, recipients);
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, MessageOverHead.getInstance());
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
