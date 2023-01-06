package thedivazo.supports.chatlistener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.mineacademy.chatcontrol.api.ChatChannelEvent;
import org.mineacademy.chatcontrol.api.PrePrivateMessageEvent;
import thedivazo.MessageOverHear;


public final class ChatControlListener implements ChatListener<ChatChannelEvent, PrePrivateMessageEvent> {

    @Override
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(ChatChannelEvent e) {
        if(!(e.getSender() instanceof Player sender)) return;
        MessageOverHear.createBubbleMessage(MessageOverHear.getConfigManager().getConfigBubble("messages"), sender, e.getMessage(), e.getRecipients());
    }

    @Override
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPrivateChat(PrePrivateMessageEvent e) {
        if (!(e.getSender() instanceof Player sender)) return;

        MessageOverHear.createBubbleMessage(MessageOverHear.getConfigManager().getConfigBubble("messages"), sender, e.getMessage(), e.getReceiver());
    }
}