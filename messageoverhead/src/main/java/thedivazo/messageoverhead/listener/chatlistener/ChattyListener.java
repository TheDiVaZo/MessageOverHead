package thedivazo.messageoverhead.listener.chatlistener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ru.mrbrikster.chatty.api.chats.Chat;
import ru.mrbrikster.chatty.api.events.ChattyMessageEvent;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.channel.ChannelFactory;

import java.util.Set;

public class ChattyListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    private void onChat(ChattyMessageEvent chatEvent) {
        Player sender = chatEvent.getPlayer();
        String message = chatEvent.getMessage();
        Chat channel = chatEvent.getChat();
        Set<Player> recipients = Set.copyOf(channel.getRecipients(sender));
        MessageOverHead.getConfigManager().getBubbleManager().spawnBubble(message, ChannelFactory.create(channel.getName()), sender, recipients);
    }
}
