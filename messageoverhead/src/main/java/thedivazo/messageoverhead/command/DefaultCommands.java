package thedivazo.messageoverhead.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import thedivazo.messageoverhead.BubbleActiveStatus;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.bubble.BubbleManager;
import thedivazo.messageoverhead.bubble.BubbleSpawned;
import thedivazo.messageoverhead.channel.ChannelFactory;

import java.util.HashSet;

@CommandAlias("moh")
public class DefaultCommands extends BaseCommand {
    @Subcommand("enable")
    @CommandPermission("moh.command.enable")
    public static void onEnable(Player player) {
        BubbleActiveStatus.setStatus(player, BubbleActiveStatus.Status.ENABLED);
        BubbleManager bubbleManager = MessageOverHead.getConfigManager().getBubbleManager();
        if (BubbleManager.getVisiblePredicate().test(player))
            bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::show);
        MessageOverHead.getConfigManager().getCommandMessageMap().get("enable").getAccess(player).ifPresent(player::sendMessage);
    }

    @Subcommand("disable")
    @CommandPermission("moh.command.disable")
    public static void onDisable(Player player) {
        BubbleActiveStatus.setStatus(player, BubbleActiveStatus.Status.DISABLED);
        BubbleManager bubbleManager = MessageOverHead.getConfigManager().getBubbleManager();
        bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::hide);
        MessageOverHead.getConfigManager().getCommandMessageMap().get("disable").getAccess(player).ifPresent(player::sendMessage);
    }

    @Subcommand("send")
    @CommandPermission("moh.command.send")
    public static void onSend(Player player, String message) {
        MessageOverHead.getConfigManager().getBubbleManager().spawnBubble(message, ChannelFactory.create("#command"), player, new HashSet<>(Bukkit.getOnlinePlayers()));
        MessageOverHead.getConfigManager().getCommandMessageMap().get("send").getAccess(player).ifPresent(player::sendMessage);
    }
}
