package thedivazo.messageoverhead.listener.chat;

import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.channel.ChannelFactory;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EqualsAndHashCode(callSuper = false)
public class DefaultChatListener implements Listener {

    Pattern privateCommandPattern = Pattern.compile("^/(w|tell|msg) (\\S+) (.+)");

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent chatEvent) {
        String message = chatEvent.getMessage();
        Player sender = chatEvent.getPlayer();
        Set<Player> recipients = chatEvent.getRecipients();
        MessageOverHead.getConfigManager().getBubbleManager().spawnBubble(message, ChannelFactory.create("all"), sender, recipients);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPrivateCommand(PlayerCommandPreprocessEvent event) {
        Matcher matcher = privateCommandPattern.matcher(event.getMessage());
        if(!matcher.matches()) return;
        Player player = Bukkit.getPlayer(matcher.group(2));
        String message = matcher.group(3);
        if(Objects.isNull(player)) return;
        MessageOverHead.getConfigManager().getBubbleManager().spawnBubble(message, ChannelFactory.create("private"), event.getPlayer(), Set.of(player));
    }
}
