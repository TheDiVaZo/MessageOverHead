package thedivazo.messageoverhead.listener.chat;

import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ru.mrbrikster.chatty.api.chats.Chat;
import ru.mrbrikster.chatty.api.events.ChattyMessageEvent;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.channel.ChannelFactory;

import java.util.Set;

@EqualsAndHashCode(callSuper = false)
public class ChattyListener extends AbstractChatListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(ChattyMessageEvent chatEvent) {
        Player sender = chatEvent.getPlayer();
        String message = chatEvent.getMessage();
        Chat channel = chatEvent.getChat();
        Set<Player> recipients = Set.copyOf(channel.getRecipients(sender));
        MessageOverHead.getConfigManager().getBubbleManager().spawnBubble(message, ChannelFactory.create(channel.getName()), sender, recipients);
    }
}
