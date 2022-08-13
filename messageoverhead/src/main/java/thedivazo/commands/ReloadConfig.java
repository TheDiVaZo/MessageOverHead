
package thedivazo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;
import thedivazo.MessageOverHear;

@CommandAlias("messageoverhear|moh")
public class ReloadConfig extends BaseCommand {

    @Subcommand("reload")
    @CommandPermission("moh.reload")
    public void onCommand(CommandSender commandSender) {
        MessageOverHear.getInstance().reloadConfig();
        MessageOverHear.getConfigPlugin().saveParam();
        commandSender.sendMessage("Config has been reloaded");

    }
}

