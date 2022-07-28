
package thedivazo.commands;

import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import thedivazo.Main;

@AllArgsConstructor
public class ReloadConfig implements CommandExecutor {

    private final Main plugin;

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("moh.reload")) {
            return false;
        }
        this.plugin.reloadConfig();
        Main.getConfigPlugin().saveParam();
        commandSender.sendMessage("Config has been reloaded");
        return true;

    }
}

