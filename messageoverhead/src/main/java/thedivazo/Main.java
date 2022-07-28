package thedivazo;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.dependency.SoftDependency;
import org.bukkit.plugin.java.annotation.dependency.SoftDependsOn;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.permission.Permissions;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import thedivazo.commands.ReloadConfig;
import thedivazo.config.Config;
import thedivazo.listener.Listeners;
import thedivazo.metrics.MetricsManager;
import thedivazo.utils.BubbleMessage;
import thedivazo.utils.BubbleMessageManager;

import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Plugin(name = "MessageOverHead", version = PluginSettings.version)
@Dependency(value = "ProtocolLib")
@SoftDependsOn(value = {
        @SoftDependency(value = "PlaceholderAPI"),
        @SoftDependency(value = "SuperVanish"),
        @SoftDependency(value = "PremiumVanish")
})
@Author(value = "TheDiVaZo")
@Commands(value = {
        @Command(name = "messageoverhead", desc = "reload configuration", permission = "moh.reload", usage = "/moh", aliases = {"moh"})
})
@Permissions(value = {
        @Permission(name = "moh.reload", desc = "reload configuration", defaultValue = PermissionDefault.OP)
})
@ApiVersion(value = ApiVersion.Target.v1_13)
public class Main extends JavaPlugin {

    private static Config configuration;

    public static Config getConfigPlugin() {
        return Main.configuration;
    }

    @Override
    public void onEnable() {
        this.checkPluginVersion();

        new MetricsManager(this);

        this.configuration = new Config(this);

        this.getServer().getPluginManager().registerEvents(new Listeners(this), this);
        this.getCommand("messageoverhead").setExecutor(new ReloadConfig(this));
    }

    @Override
    public void onDisable() {
        BubbleMessageManager.getBubbleMessageMap().values().forEach(BubbleMessage::remove);
    }

    private void checkPluginVersion() {
        if (!PluginSettings.version.equals(Config.getLastVersionOfPlugin())) {
            for (int i = 0; i < 5; i++) {
                Bukkit.getLogger().warning("PLEASE, UPDATE MESSAGE OVER HEAR! LINK: https://www.spigotmc.org/resources/messageoverhead-pop-up-messages-above-your-head-1-13-1-18.100051/");
            }
        } else {
            Bukkit.getLogger().info("Plugin have last version");
        }
    }

    public static Float getVersion() {
        String version = Bukkit.getVersion();
        Pattern pattern = Pattern.compile("\\(MC: ([0-9]+\\.[0-9]+)");
        Matcher matcher = pattern.matcher(version);
        if (matcher.find())
        {
            return Float.parseFloat(matcher.group(1));
        }
        else return null;
    }

}

