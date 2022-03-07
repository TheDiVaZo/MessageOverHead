package thedivazo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
import thedivazo.listener.Listeners;
import thedivazo.metrics.Metrics;
import thedivazo.utils.BubbleMessage;

@Plugin(name="MessageOverHead", version=PluginSettings.version)
@Dependency(value="ProtocolLib")
@SoftDependsOn(value={
        @SoftDependency(value="PlaceholderAPI"),
        @SoftDependency(value="SuperVanish"),
        @SoftDependency(value="PremiumVanish")
})
@Author(value="TheDiVaZo")
@Commands(value={
        @Command(name="messageoverhead", desc="reload configuration", permission="moh.reload", usage="/moh", aliases={"moh"})
})
@Permissions(value={
        @Permission(name="moh.reload", desc="reload configuration", defaultValue=PermissionDefault.OP)
})
@ApiVersion(value=ApiVersion.Target.v1_13)

public class Main extends JavaPlugin {

    public final HashMap<UUID, BubbleMessage> bubbleMessageMap = new HashMap<UUID, BubbleMessage>();

    public boolean isPAPILoaded = false;
    public boolean isSuperVanishLoaded = false;

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

    public static HashMap<Integer, String[]> permissionFormat = new HashMap<Integer, String[]>();

    public int distance = 10;
    public double biasY = 2.15;

    public boolean isVisibleTextForOwner = false;

    public String permSend = "moh.send";
    public String permSee = "moh.see";

    public int delay = 4;
    public int sizeLine = 24;
    public static final Pattern HEXPAT = Pattern.compile("&#[a-fA-F0-9]{6}");

    public void onEnable() {
        this.checkPluginVersion();
        this.enableMetrics();
        this.saveDefaultConfig();
        this.updateConfig();
        this.saveParam();
        this.getServer().getPluginManager().registerEvents(new Listeners(this), this);
        this.getCommand("moh").setExecutor(new ReloadConfig(this));
        this.getCommand("messageoverhead").setExecutor(new ReloadConfig(this));
    }

    private void enableMetrics() {
        Metrics metrics = new Metrics(this, 14530);
        metrics.addCustomChart(new Metrics.DrilldownPie("java_version", () -> {
            HashMap<String, Map<String, Integer>> map = new HashMap();
            String javaVersion = System.getProperty("java.version");
            HashMap<String, Integer> entry = new HashMap<String, Integer>();
            entry.put(javaVersion, 1);
            if (javaVersion.startsWith("1.13")) {
                map.put("Java 1.13", entry);
            } else if (javaVersion.startsWith("1.14")) {
                map.put("Java 1.14", entry);
            } else if (javaVersion.startsWith("1.15")) {
                map.put("Java 1.15", entry);
            } else if (javaVersion.startsWith("1.16")) {
                map.put("Java 1.16", entry);
            } else if (javaVersion.startsWith("1.17")) {
                map.put("Java 1.17", entry);
            } else if (javaVersion.startsWith("1.18")) {
                map.put("Java 1.18", entry);
            } else {
                map.put("Other", entry);
            }
            return map;
        }));
    }

    private void checkPluginVersion() {
        if (!PluginSettings.version.equals(this.getLastVersionOfPlugin())) {
            Bukkit.getLogger().warning("PLEASE, UPDATE MESSAGE OVER HEAR! LINK: https://www.spigotmc.org/resources/messageoverhead-pop-up-messages-above-your-head-1-13-1-18.100051/");
            Bukkit.getLogger().warning("PLEASE, UPDATE MESSAGE OVER HEAR! LINK: https://www.spigotmc.org/resources/messageoverhead-pop-up-messages-above-your-head-1-13-1-18.100051/");
            Bukkit.getLogger().warning("PLEASE, UPDATE MESSAGE OVER HEAR! LINK: https://www.spigotmc.org/resources/messageoverhead-pop-up-messages-above-your-head-1-13-1-18.100051/");
            Bukkit.getLogger().warning("PLEASE, UPDATE MESSAGE OVER HEAR! LINK: https://www.spigotmc.org/resources/messageoverhead-pop-up-messages-above-your-head-1-13-1-18.100051/");
            Bukkit.getLogger().warning("PLEASE, UPDATE MESSAGE OVER HEAR! LINK: https://www.spigotmc.org/resources/messageoverhead-pop-up-messages-above-your-head-1-13-1-18.100051/");
            Bukkit.getLogger().warning("PLEASE, UPDATE MESSAGE OVER HEAR! LINK: https://www.spigotmc.org/resources/messageoverhead-pop-up-messages-above-your-head-1-13-1-18.100051/");
        } else {
            Bukkit.getLogger().info("Plugin have last version");
        }
    }

    private String getLastVersionOfPlugin() {
        try {
            String inputLine;
            Bukkit.getLogger().info("Check updates...");
            URL obj = new URL("https://api.spigotmc.org/legacy/update.php?resource=100051");
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (IOException e) {
            Bukkit.getLogger().warning("ERROR GETTING LAST VERSION!");
            return "error";
        }
    }

    public void saveParam() {
        FileConfiguration config;
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            Bukkit.getLogger().warning("ProtocolLib not found! Please install ProtocolLib");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.isPAPILoaded = true;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish")) {
            this.isSuperVanishLoaded = true;
        }
        if ((config = this.getConfig()).getBoolean("messages.particle.enable")) {
            this.particleEnable = true;
            try {
                this.particleType = Particle.valueOf(config.getString("messages.particle.particleType"));
            }
            catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("Wrong particle type. Please check your config. Default particle type set: CLOUD");
            }
            this.particleCount = config.getInt("messages.particle.count");
            this.particleOffsetX = config.getDouble("message.particle.offsetX");
            this.particleOffsetY = config.getDouble("message.particle.offsetY");
            this.particleOffsetZ = config.getDouble("message.particle.offsetZ");
        }
        if (config.getBoolean("messages.sound.enable")) {
            try {
                this.soundType = Sound.valueOf(config.getString("messages.sound.soundType"));
            }
            catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("Wrong sound type. Please check your config. Default sound type set: BLOCK_ANVIL_STEP");
            }
            this.soundVolume = config.getInt("messages.sound.volume");
            this.soundPitch = config.getInt("messages.sound.pitch");
        }
        if (config.isString("messages.settings.format")) {
            format = config.getString("messages.settings.format");
        } else {
            ConfigurationSection permissionsFormat = config.getConfigurationSection("messages.settings.format");
            for (String priority : permissionsFormat.getKeys(false)) {
                String[] permFormat = new String[2];
                permFormat[1] = permissionsFormat.getConfigurationSection(priority).getString("format");
                if (permissionsFormat.getConfigurationSection(priority).isString("perm")) {
                    permFormat[0] = permissionsFormat.getConfigurationSection(priority).getString("perm");
                }
                permissionFormat.put(Integer.parseInt(priority), permFormat);
            }
            format = "%player_name% %message%";
        }
        this.distance = config.getInt("messages.settings.distance");
        this.biasY = config.getDouble("messages.settings.biasY");
        this.isVisibleTextForOwner = config.getBoolean("messages.settings.visibleTextForOwner");
        this.permSend = config.getString("messages.settings.permSend");
        this.permSee = config.getString("messages.settings.permSee");
        this.delay = config.getInt("messages.settings.delay");
        this.sizeLine = config.getInt("messages.settings.sizeLine");
    }

    public void onDisable() {
        this.bubbleMessageMap.values().forEach(BubbleMessage::remove);
    }

    public static String makeColors(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (Main.getVersion().floatValue() < 1.16f) {
            return message;
        }
        Matcher match = HEXPAT.matcher(message);
        while (match.find()) {
            String color = message.substring(match.start(), match.end());
            message = message.replace(color, "" + ChatColor.of(color.replace("&", "")));
        }
        return message;
    }

    public static Float getVersion() {
        String version = Bukkit.getVersion();
        String pattern = "[^0-9\\.\\:]";
        String versionMinecraft = version.replaceAll(pattern, "");
        return Float.parseFloat(versionMinecraft.substring(versionMinecraft.indexOf(":") + 1, versionMinecraft.lastIndexOf(".")));
    }

    public void updateConfig() {
        try {
            URL linkDefaultConfig = new URL("https://raw.githubusercontent.com/TheDiVaZo/MessageOverHead/master/config.yml");
            URLConnection connectionDefaultConfig = linkDefaultConfig.openConnection();
            connectionDefaultConfig.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; .NET CLR 1.2.30703)");
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(connectionDefaultConfig.getInputStream()));
            FileConfiguration thisConfig = this.getConfig();
            if (defaultConfig.getDouble("version") != thisConfig.getDouble("version")) {
                Set<String> DCKeys = defaultConfig.getKeys(true);
                Set<String> TCKeys = thisConfig.getKeys(true);
                if (!TCKeys.containsAll(DCKeys)) {
                    for (String key : DCKeys) {
                        if (TCKeys.contains(key)) continue;
                        thisConfig.set(key, defaultConfig.get(key));
                    }
                }
                thisConfig.set("version", defaultConfig.get("version"));
            }
            this.saveConfig();
        }
        catch (IOException e) {
            Bukkit.getLogger().warning("UPDATE CONFIG ERROR!");
        }
    }
}

