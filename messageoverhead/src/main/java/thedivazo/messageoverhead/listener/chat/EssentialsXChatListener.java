package thedivazo.messageoverhead.listener.chat;

import net.essentialsx.api.v2.events.chat.GlobalChatEvent;
import net.essentialsx.api.v2.events.chat.LocalChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.channel.ChannelFactory;

import java.util.Set;

public class EssentialsXChatListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onGlobalChat(GlobalChatEvent globalChatEvent) {
        String message = globalChatEvent.getMessage();
        Player sender = globalChatEvent.getPlayer();
        Set<Player> recipients = globalChatEvent.getRecipients();
        MessageOverHead.CONFIG_MANAGER.getBubbleManager().spawnBubble(message, ChannelFactory.create("global"), sender, recipients);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLocalChat(LocalChatEvent globalChatEvent) {
        String message = globalChatEvent.getMessage();
        Player sender = globalChatEvent.getPlayer();
        Set<Player> recipients = globalChatEvent.getRecipients();
        MessageOverHead.CONFIG_MANAGER.getBubbleManager().spawnBubble(message, ChannelFactory.create("local"), sender, recipients);
    }

//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onLocalChatSpy(LocalChatSpyEvent globalChatEvent) {
//        String message = globalChatEvent.getMessage();
//        Set<Player> recipients = globalChatEvent.getRecipients();
//        for (Player recipient : recipients) {
//            MessageOverHead.getConfigManager().getBubbleManager().spawnBubble(message, ChannelFactory.create("#spy"), recipient, Collections.emptySet());
//        }
//    }
}
