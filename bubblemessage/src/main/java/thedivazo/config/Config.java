package thedivazo.config;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import thedivazo.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

@Data
public class Config {
    private final Main plugin;
    private FileConfiguration config;


    private boolean isPAPILoaded = false;

    private boolean isSuperVanishLoaded = false;

    // всегда True. Ctrl + Shift + F и смотри где у тебя используется particleEnable
    // ответ - он всегда будет true.
    private boolean isParticleEnable = true;
    private Particle particleType = Particle.CLOUD;
    private int particleCount = 4;
    private double particleOffsetX = 0.2;
    private double particleOffsetY = 0.2;
    private double particleOffsetZ = 0.2;

    // Аналогично isParticleEnable
    private boolean isSoundEnable = true;
    private Sound soundType = Sound.BLOCK_ANVIL_STEP;
    private int soundVolume = 4;
    private int soundPitch = 4;

    private String messageFormat = "%player_name% %message%";

    private final HashMap<Integer, String[]> permissionFormat = new HashMap<>();

    private int distance = 10;
    private double biasY = 2.15;

    private boolean isVisibleTextForOwner = false;

    private String permSend = "moh.send";
    private String permSee = "moh.see";

    private int delay = 4;
    private int sizeLine = 24;
    private static final Pattern HEXPAT = Pattern.compile("&#[a-fA-F0-9]{6}");

    public Config(Main plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        plugin.saveDefaultConfig();
        updateConfig();
        saveParam();
    }

    public void saveParam() {
        this.config = plugin.getConfig();
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            Bukkit.getLogger().warning("ProtocolLib not found! Please install ProtocolLib");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.setPAPILoaded(true);
        }
        if (Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish")) {
            this.setSuperVanishLoaded(true);
        }
        if (config.getBoolean("messages.particle.enable")) {
            this.setParticleEnable(true);
            try {
                this.setParticleType(Particle.valueOf(config.getString("messages.particle.particleType")));
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("Wrong particle type. Please check your config. Default particle type set: CLOUD");
            }
            this.setParticleCount(config.getInt("messages.particle.count"));
            this.setParticleOffsetX(config.getDouble("message.particle.offsetX"));
            this.setParticleOffsetY(config.getDouble("message.particle.offsetY"));
            this.setParticleOffsetZ(config.getDouble("message.particle.offsetZ"));
        }
        if (config.getBoolean("messages.sound.enable")) {
            try {
                this.soundType = Sound.valueOf(config.getString("messages.sound.soundType"));
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("Wrong sound type. Please check your config. Default sound type set: BLOCK_ANVIL_STEP");
            }
            this.setSoundVolume(config.getInt("messages.sound.volume"));
            this.setSoundPitch(config.getInt("messages.sound.pitch"));
        }
        if (config.isString("messages.settings.format")) {
            this.setMessageFormat(config.getString("messages.settings.format"));
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
            this.setMessageFormat("%player_name% %message%");
        }

        this.setParticleEnable(config.getBoolean("messages.particle.enable"));
        this.setSoundEnable(config.getBoolean("messages.sound.enable"));
        this.setDistance(config.getInt("messages.settings.distance"));
        this.setBiasY(config.getDouble("messages.settings.biasY"));
        this.setVisibleTextForOwner(config.getBoolean("messages.settings.visibleTextForOwner"));
        this.setPermSend(config.getString("messages.settings.permSend"));
        this.setPermSee(config.getString("messages.settings.permSee"));
        this.setDelay(config.getInt("messages.settings.delay"));
        this.setSizeLine(config.getInt("messages.settings.sizeLine"));
    }

    public static String getLastVersionOfPlugin() {
        String inputLine;
        Bukkit.getLogger().info("Check updates...");
        try {
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

    public void updateConfig() {
        try {
            URL linkDefaultConfig = new URL("https://raw.githubusercontent.com/TheDiVaZo/MessageOverHead/master/config.yml");
            URLConnection connectionDefaultConfig = linkDefaultConfig.openConnection();
            connectionDefaultConfig.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; .NET CLR 1.2.30703)");
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(connectionDefaultConfig.getInputStream()));
            FileConfiguration thisConfig = plugin.getConfig();
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
                plugin.saveConfig();
            }
        } catch (IOException e) {
            Bukkit.getLogger().warning("UPDATE CONFIG ERROR!!");
        }
    }

}
