package thedivazo.messageoverhead.listener.chat;

import lombok.EqualsAndHashCode;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.api.events.VentureChatEvent;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.channel.ChannelFactory;

import java.util.Set;

@EqualsAndHashCode(callSuper = false)
public class VentureChatListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(VentureChatEvent eventChat) {
        MineverseChatPlayer chatPlayer = eventChat.getMineverseChatPlayer();
        String message = eventChat.getChat();
        Set<Player> recipients = eventChat.getRecipients();
        ChatChannel channel = eventChat.getChannel();
        MessageOverHead.getConfigManager().getBubbleManager().spawnBubble(message, ChannelFactory.create(channel.getName()), chatPlayer.getPlayer(), recipients);
    }
}
