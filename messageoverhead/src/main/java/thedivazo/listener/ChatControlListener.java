package thedivazo.listener;

import api.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.mineacademy.chatcontrol.api.ChatChannelEvent;
import org.mineacademy.chatcontrol.api.PrePrivateMessageEvent;
import thedivazo.MessageOverHear;


public class ChatControlListener implements ChatListener<ChatChannelEvent, PrePrivateMessageEvent> {

    @Override
    @EventHandler
    public void onPlayerChat(ChatChannelEvent e) {
        if(!(e.getSender() instanceof Player sender)) return;
        MessageOverHear.createBubbleMessage(sender, e.getMessage(), e.getRecipients());
    }

    @Override
    @EventHandler
    public void onPlayerPrivateChat(PrePrivateMessageEvent e) {
        if (!(e.getSender() instanceof Player player)) return;

        MessageOverHear.createBubbleMessage(player, e.getMessage(), e.getReceiver());
    }
}