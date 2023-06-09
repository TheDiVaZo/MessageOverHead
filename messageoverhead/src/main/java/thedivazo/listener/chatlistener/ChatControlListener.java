package thedivazo.listener.chatlistener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.mineacademy.chatcontrol.api.ChatChannelEvent;
import org.mineacademy.chatcontrol.api.PrePrivateMessageEvent;
import thedivazo.MessageOverHead;


public final class ChatControlListener implements ChatListener<ChatChannelEvent, PrePrivateMessageEvent> {

    @Override
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(ChatChannelEvent e) {
        if(!(e.getSender() instanceof Player)) return;
        //MessageOverHead.createBubbleMessage(MessageOverHead.getConfigManager().getConfigBubble("messages"), (Player) e.getSender(), e.getMessage(), e.getRecipients());
    }

    @Override
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPrivateChat(PrePrivateMessageEvent e) {
        if (!(e.getSender() instanceof Player)) return;

        //MessageOverHead.createBubbleMessage(MessageOverHead.getConfigManager().getConfigBubble("messages"), (Player) e.getSender(), e.getMessage(), e.getReceiver());
    }

    @Override
    public void disableListener() {
        PrePrivateMessageEvent.getHandlerList().unregister(MessageOverHead.getInstance());
        ChatChannelEvent.getHandlerList().unregister(MessageOverHead.getInstance());
    }
}