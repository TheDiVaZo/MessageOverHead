package thedivazo.messageoverhead.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.bubble.BubbleGenerator;
import java.util.HashSet;

@CommandAlias("amoh")
public class AdminCommands extends BaseCommand {
    @Subcommand("create")
    @CommandPermission("amoh.command.create")
    public static void onCreate(Player player, BubbleGenerator bubbleGenerator, String message) {
        MessageOverHead.getConfigManager().getBubbleManager().spawnBubble(message, bubbleGenerator, player, new HashSet<>(Bukkit.getOnlinePlayers()));
        MessageOverHead.getConfigManager().getCommandMessageMap().get("create").getAccess().ifPresent(player::sendMessage);
    }
}
