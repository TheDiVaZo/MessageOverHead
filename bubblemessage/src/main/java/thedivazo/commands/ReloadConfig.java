
package thedivazo.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import thedivazo.Main;
import thedivazo.config.Config;

public class ReloadConfig implements CommandExecutor {

    private final Main plugin;
    private final Config config;

    public ReloadConfig(Main plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("moh.reload")) {
            return false;
        }
        this.plugin.reloadConfig();

        config.saveParam();
        commandSender.sendMessage("Config has been reloaded");
        return true;

    }
}

