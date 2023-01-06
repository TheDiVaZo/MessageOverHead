package thedivazo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;
import thedivazo.MessageOverHear;

@CommandAlias("messageoverhear|moh")
public class DebugCommands extends BaseCommand {

    @Subcommand("config")
    @CommandPermission("moh.develop.config")
    public void checkConfig(CommandSender sender) {
        sender.sendMessage(MessageOverHear.getConfigManager().toString());
    }
}
