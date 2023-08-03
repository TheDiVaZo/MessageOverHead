package thedivazo.messageoverhead.listener.chat;

import lombok.EqualsAndHashCode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.mineacademy.chatcontrol.api.ChatChannelEvent;
import org.mineacademy.chatcontrol.api.PrePrivateMessageEvent;
import org.mineacademy.chatcontrol.model.Channel;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.channel.ChannelFactory;

import java.util.Objects;
import java.util.Set;

@EqualsAndHashCode(callSuper = false)
public class ChatControlRedListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(ChatChannelEvent chatEvent) {
        String message = chatEvent.getMessage();
        CommandSender sender = chatEvent.getSender();
        if (!(sender instanceof Player)) return;
        Set<Player> recipients = chatEvent.getRecipients();
        Channel channel = chatEvent.getChannel();
        MessageOverHead.getConfigManager().getBubbleManager().spawnBubble(message, ChannelFactory.create(channel.getName()), (Player) sender, recipients);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPrivateCommand(PrePrivateMessageEvent event) {
        CommandSender sender = event.getSender();
        if (!(sender instanceof Player)) return;
        Player receiver = event.getReceiver();
        if(Objects.isNull(receiver)) return;
        String message = event.getMessage();
        MessageOverHead.getConfigManager().getBubbleManager().spawnBubble(message, ChannelFactory.create("#private"), (Player) sender, Set.of(receiver));
    }

//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onSpy(SpyEvent event) {
//        if (event.getType() != Spy.Type.CHAT && event.getType() != Spy.Type.PRIVATE_MESSAGE) return;
//        CommandSender sender = event.getSender();
//        if (sender == null || !(event.getSender() instanceof Player)) return;
//        for (Player recipient : event.getRecipients()) {
//            MessageOverHead.getConfigManager().getBubbleManager().spawnBubble(event.getMessage(), ChannelFactory.create("#spy"), recipient, Collections.emptySet());
//        }
//    }
}
