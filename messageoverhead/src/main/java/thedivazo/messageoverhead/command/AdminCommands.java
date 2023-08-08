package thedivazo.messageoverhead.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.bubble.BubbleGenerator;
import thedivazo.messageoverhead.logging.Logger;

import java.util.HashSet;

@CommandAlias("amoh")
public class AdminCommands extends BaseCommand {
    @Subcommand("create")
    @CommandPermission("amoh.command.create")
    @CommandCompletion("@bubble-generators")
    public static void onCreate(Player player, BubbleGenerator bubbleGenerator, String message) {
        MessageOverHead.CONFIG_MANAGER.getBubbleManager().spawnBubble(message, bubbleGenerator, player, new HashSet<>(Bukkit.getOnlinePlayers()));
        MessageOverHead.CONFIG_MANAGER.getCommandMessageMap().get("create").getAccess(player).ifPresent(player::sendMessage);
    }

    @Subcommand("reload")
    @CommandPermission("amoh.command.reload")
    public static void onReload(CommandSender commandSender) {
        try {
            MessageOverHead.reloadConfigManager();
            commandSender.sendMessage(ChatColor.GREEN + "Config gas been reloaded");
        } catch (InvalidConfigurationException e) {
            commandSender.sendMessage(ChatColor.RED + "Check the configuration for errors or typos");
            Logger.error(e.getMessage(), e);
            commandSender.sendMessage(e.getMessage());
        }
    }
}
