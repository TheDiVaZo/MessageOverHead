
package io.messageoverhead.thedivazo;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class reloadConfig
implements CommandExecutor {
    private final JavaPlugin plugin;

    public reloadConfig(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("moh.reload")) {
            return false;
        }
        this.plugin.reloadConfig();
        ((thisPlug)this.plugin).saveParam();
        commandSender.sendMessage("Config has been reloaded");
        return true;
    }
}

