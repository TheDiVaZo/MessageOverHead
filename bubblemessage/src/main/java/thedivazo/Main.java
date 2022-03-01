
package thedivazo;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.dependency.SoftDependency;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.permission.Permissions;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import thedivazo.listener.Listeners;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Это просто вместо plugin.yml. Если на понравится, можешь вернуть обратно как было.
@Plugin(name = "MessageOverHead", version = "2.2")
@Dependency("ProtocolLib")
@SoftDependency("PlaceholderAPI")
@Author("TheDiVaZo")
@Commands(@Command(name = "messageoverhead", desc = "reload configuration", permission = "moh.reload", usage = "/moh", aliases = {"moh"}))
@Permissions(@Permission(name = "moh.reload", desc = "reload configuration", defaultValue = PermissionDefault.OP))
@ApiVersion(ApiVersion.Target.v1_13)
public class Main extends JavaPlugin {

    public final HashMap<UUID, BubbleMessage> bubbleMessageMap = new HashMap<>();

    public boolean isPAPILoaded = false;

    public boolean particleEnable = true;

    public Particle particleType = Particle.CLOUD;
    public int particleCount = 4;
    public double particleOffsetX = 0.2;
    public double particleOffsetY = 0.2;
    public double particleOffsetZ = 0.2;

    public boolean soundEnable = true;
    public Sound soundType = Sound.BLOCK_ANVIL_STEP;
    public int soundVolume = 4;
    public int soundPitch = 4;

    public static String format = "%player_name% %message%";
    public static HashMap<Integer, String[]> permissionFormat = new HashMap<>();
    public int distance = 10;
    public double biasY = 2.15;

    public boolean isVisibleTextForOwner = false;
    public String permSend = "moh.send";
    public String permSee = "moh.see";

    public int delay = 4;
    public int sizeLine = 24;


    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.saveParam();
        this.getServer().getPluginManager().registerEvents(new Listeners(this), this);
        this.getCommand("moh").setExecutor(new ReloadConfig(this));
        this.getCommand("messageoverhead").setExecutor(new ReloadConfig(this));
    }

    public void saveParam() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            Bukkit.getLogger().warning("ProtocolLib not found! Please install ProtocolLib");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            isPAPILoaded = true;
        }
        FileConfiguration config = getConfig();

        if (config.getBoolean("messages.particle.enable")) {
            particleEnable = true;
            try {
                particleType = Particle.valueOf(config.getString("messages.particle.particleType"));
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("Wrong particle type. Please check your config. Default particle type set: CLOUD");
            }
            particleCount = config.getInt("messages.particle.count");
            particleOffsetX = config.getDouble("message.particle.offsetX");
            particleOffsetY = config.getDouble("message.particle.offsetY");
            particleOffsetZ = config.getDouble("message.particle.offsetZ");
        }

        if (config.getBoolean("messages.sound.enable")) {
            try {
                soundType = Sound.valueOf(config.getString("messages.sound.soundType"));
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("Wrong sound type. Please check your config. Default sound type set: BLOCK_ANVIL_STEP");
            }
            soundVolume = config.getInt("messages.sound.volume");
            soundPitch = config.getInt("messages.sound.pitch");
        }

        if(config.isString("messages.settings.format")) {
            format = config.getString("messages.settings.format");
        }
        else {
            ConfigurationSection permissionsFormat = config.getConfigurationSection("messages.settings.format");
            for(String priority:permissionsFormat.getKeys(false)) {
                String[] permFormat = new String[2];
                permFormat[1] = permissionsFormat.getConfigurationSection(priority).getString("format");
                if(permissionsFormat.getConfigurationSection(priority).isString("perm")) {
                    permFormat[0] = permissionsFormat.getConfigurationSection(priority).getString("perm");
                }
                permissionFormat.put(
                        Integer.parseInt(priority),
                        permFormat);
            }
            format = "%player_name% %message%";
        }
        distance = config.getInt("messages.settings.distance");
        biasY = config.getDouble("messages.settings.biasY");
        isVisibleTextForOwner = config.getBoolean("messages.settings.visibleTextForOwner");
        permSend = config.getString("messages.settings.permSend");
        permSee = config.getString("messages.settings.permSee");
        delay = config.getInt("messages.settings.delay");
        sizeLine = config.getInt("messages.settings.sizeLine");
    }

    @Override
    public void onDisable() {
        bubbleMessageMap.values().forEach(BubbleMessage::remove);
    }


    public final static Pattern HEXPAT = Pattern.compile("&#[a-fA-F0-9]{6}");

    public static String makeColors(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (getVersion() < 1.16f) return message;
        Matcher match = HEXPAT.matcher(message);
        while (match.find()) {
            String color = message.substring(match.start(), match.end());
            message = message.replace(color, ChatColor.of(color.replace("&", "")) + "");
        }

        return message;
    }
    public static Float getVersion() {
        String version = Bukkit.getVersion();
        String pattern = "[^0-9\\.\\:]";
        String versionMinecraft = version.replaceAll(pattern, "");
        return Float.parseFloat(versionMinecraft.substring(
                versionMinecraft.indexOf(":")+1,
                versionMinecraft.lastIndexOf(".")));

    }


}

