package thedivazo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import thedivazo.MessageOverHear;

@CommandAlias("messageoverhear|moh")
public class DefaultCommands extends BaseCommand {

    @Subcommand("send")
    @Syntax("<сообщение>")
    public void onCommand(Player player, String message) {
        MessageOverHear.createBubbleMessage(MessageOverHear.getConfigManager().getConfigBubble("messages"), player.getPlayer(), message);
    }

    @Subcommand("reload")
    @CommandPermission("moh.reload")
    public void onCommand(CommandSender commandSender) {
        MessageOverHear.getInstance().reloadConfigManager();
        commandSender.sendMessage("Config has been reloaded");
    }
}
