
package thedivazo;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadConfig implements CommandExecutor {

    private final Main plugin;

    public ReloadConfig(Main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("moh.reload")) {
            return false;
        }
        this.plugin.reloadConfig();

        plugin.saveParam();
        commandSender.sendMessage("Config has been reloaded");
        return true;
    }
}

