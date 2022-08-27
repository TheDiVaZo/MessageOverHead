package thedivazo.config;

import api.logging.Logger;
import de.myzelyam.api.vanish.VanishAPI;
import lombok.Data;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import thedivazo.MessageOverHear;
import thedivazo.supports.chatlistener.ChatControlListener;
import thedivazo.supports.chatlistener.ChatListener;
import thedivazo.supports.chatlistener.DefaultChatListener;
import thedivazo.supports.vanish.SuperVansihManager;
import thedivazo.supports.vanish.VanishManager;
import thedivazo.utils.ConfigUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import static org.bukkit.Bukkit.getServer;

@Data
public class ConfigManager {
    private final MessageOverHear plugin;
    private FileConfiguration fileConfig;
    private ConfigUtils configUtils;

    private boolean isPAPILoaded = false;
    private boolean isIALoaded = false; //IA - ItemsAdder
    private boolean isOraxenLoaded = false;
    private boolean isVault = false;

    private boolean isParticleEnable = true;
    private Particle particleType = Particle.CLOUD;
    private int particleCount = 4;
    private double particleOffsetX = 0.2;
    private double particleOffsetY = 0.2;
    private double particleOffsetZ = 0.2;

    private boolean isSoundEnable = true;
    private Sound soundType = Sound.BLOCK_ANVIL_STEP;
    private int soundVolume = 4;
    private int soundPitch = 4;

    private Permission permissionVault = null;

    private ChatListener<?, ?> chatEventListener;
    private VanishManager vanishManager;

    public static final String DEFAULT_MESSAGE_FORMAT = "%player_name% %message%";

    private final LinkedHashMap<Integer, Format> messageFormat = new LinkedHashMap<>();

    public String toString() {
        return fileConfig.saveToString();
    }

    public static class Format {
        private final String permission;
        private final ArrayList<String> formatsMessage = new ArrayList<>();

        public List<String> getFormatsMessage() {
            return formatsMessage;
        }

        public String getPermission() {
            return permission;
        }

        public Format(String formatMessage, String permission) {
            this.formatsMessage.add(formatMessage);
            this.permission = permission;
        }
        public Format(List<String> formatsMessage, String permission) {
            this.formatsMessage.addAll(formatsMessage);
            this.permission = permission;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Format)) return false;
            Format format = (Format) o;
            return Objects.equals(getFormatsMessage(), format.getFormatsMessage()) && Objects.equals(getPermission(), format.getPermission());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getFormatsMessage(), getPermission());
        }
    }

    private int distance = 10;
    private double biasY = 2.15;

    private boolean isVisibleTextForOwner = false;

    private String permSend = "moh.send";
    private String permSee = "moh.see";

    private int delay = 4;
    private int sizeLine = 24;

    public ConfigManager(MessageOverHear plugin) {
        this.plugin = plugin;
        this.fileConfig = plugin.getConfig();
        this.configUtils = new ConfigUtils(fileConfig);
        plugin.saveDefaultConfig();
        updateConfig();
        saveParam();
    }

    private boolean isPlugin(String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName) != null;
    }

    private void saveFormatFromConfig() {
        messageFormat.clear();
        ConfigurationSection permissionsFormat = fileConfig.getConfigurationSection("messages.settings.format");

        if (permissionsFormat == null) {
            List<String> formatsMessage = configUtils.getAlwaysListString("messages.settings.format");
            if(!formatsMessage.isEmpty()) messageFormat.put(0, new Format(formatsMessage, null));
        } else {
            for (String priority : permissionsFormat.getKeys(false)) {
                ConfigurationSection sectionFormat = permissionsFormat.getConfigurationSection(priority);
                if (sectionFormat != null) {
                    List<String> formatsMessage = configUtils.getAlwaysListString("format", sectionFormat).stream().filter(Objects::nonNull).toList();
                    if (!formatsMessage.isEmpty()) {
                        String perm = sectionFormat.getString("perm");
                        messageFormat.put(Integer.parseInt(priority), new Format(formatsMessage, perm));
                    }
                }
            }
        }
    }

    private void saveChatEventListener() {
        if (isPlugin("ChatControlRed")) {
            this.setChatEventListener(new ChatControlListener());
        }
        else this.setChatEventListener(new DefaultChatListener());
    }

    private void saveVanishManager() {
        if (isPlugin("SuperVanish") || isPlugin("PremiumVanish")) {
            this.setVanishManager(new SuperVansihManager());
        }
    }

    private void saveSoftDependCondition() {
        if (!isPlugin("ProtocolLib")) {
            Logger.warn("ProtocolLib not found! Please install ProtocolLib");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
        if (isPlugin("ItemsAdder")) {
            this.setIALoaded(true);
        }
        if (isPlugin("Oraxen")) {
            this.setOraxenLoaded(true);
        }
        if(isPlugin("Vault")) {
            this.setVault(true);
            RegisteredServiceProvider<Permission> rsp1 = getServer().getServicesManager().getRegistration(Permission.class);
            if(rsp1!= null) {
                setPermissionVault(rsp1.getProvider());
            }
        }
        saveChatEventListener();
    }

    public void saveParam() {
        this.fileConfig = plugin.getConfig();
        this.configUtils.setConfig(fileConfig);
        saveFormatFromConfig();
        saveSoftDependCondition();
        if (fileConfig.getBoolean("messages.particle.enable")) {
            this.setParticleEnable(true);
            try {
                this.setParticleType(Particle.valueOf(fileConfig.getString("messages.particle.particleType")));
            } catch (IllegalArgumentException e) {
                Logger.warn("Wrong particle type. Please check your config. Default particle type set: CLOUD");
            }
            this.setParticleCount(fileConfig.getInt("messages.particle.count"));
            this.setParticleOffsetX(fileConfig.getDouble("message.particle.offsetX"));
            this.setParticleOffsetY(fileConfig.getDouble("message.particle.offsetY"));
            this.setParticleOffsetZ(fileConfig.getDouble("message.particle.offsetZ"));
        }
        if (fileConfig.getBoolean("messages.sound.enable")) {
            try {
                this.soundType = Sound.valueOf(fileConfig.getString("messages.sound.soundType"));
            } catch (IllegalArgumentException e) {
                Logger.warn("Wrong sound type. Please check your config. Default sound type set: BLOCK_ANVIL_STEP");
            }
            this.setSoundVolume(fileConfig.getInt("messages.sound.volume"));
            this.setSoundPitch(fileConfig.getInt("messages.sound.pitch"));
        }

        this.setParticleEnable(fileConfig.getBoolean("messages.particle.enable"));
        this.setSoundEnable(fileConfig.getBoolean("messages.sound.enable"));
        this.setDistance(fileConfig.getInt("messages.settings.distance"));
        this.setBiasY(fileConfig.getDouble("messages.settings.biasY"));
        this.setVisibleTextForOwner(fileConfig.getBoolean("messages.settings.visibleTextForOwner"));
        this.setPermSend(fileConfig.getString("messages.settings.permSend"));
        this.setPermSee(fileConfig.getString("messages.settings.permSee"));
        this.setDelay(fileConfig.getInt("messages.settings.delay"));
        this.setSizeLine(fileConfig.getInt("messages.settings.sizeLine"));
    }

    public static String getLastVersionOfPlugin() {
        String inputLine;
        Logger.info("Check updates...");
        try {
            URL obj = new URL("https://api.spigotmc.org/legacy/update.php?resource=100051");
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (IOException e) {
            Logger.warn("ERROR GETTING LAST VERSION!");
            return "error";
        }
    }

    public void updateConfig() {
        try {
            String pathVersionSection = "version";
            URL linkDefaultConfig = new URL("https://raw.githubusercontent.com/TheDiVaZo/MessageOverHead/master/config.yml");
            URLConnection connectionDefaultConfig = linkDefaultConfig.openConnection();
            connectionDefaultConfig.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; .NET CLR 1.2.30703)");
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(connectionDefaultConfig.getInputStream()));
            FileConfiguration thisConfig = plugin.getConfig();
            if (defaultConfig.getDouble(pathVersionSection) != thisConfig.getDouble(pathVersionSection)) {
                Set<String> defaultConfigKeys = defaultConfig.getKeys(true);
                Set<String> thisConfigKeys = thisConfig.getKeys(true);
                if (!thisConfigKeys.containsAll(defaultConfigKeys)) {
                    for (String key : defaultConfigKeys) {
                        if (thisConfigKeys.contains(key)) continue;
                        thisConfig.set(key, defaultConfig.get(key));
                    }
                }
                thisConfig.set(pathVersionSection, defaultConfig.get(pathVersionSection));
                plugin.saveConfig();
            }
        } catch (IOException e) {
            Logger.warn("UPDATE CONFIG ERROR!!");
        }
    }

    public static List<String> getFormatOfPlayer(Player player) {
        List<String> defaultFormat = new ArrayList<>();
        defaultFormat.add(ConfigManager.DEFAULT_MESSAGE_FORMAT);

        for (Map.Entry<Integer, ConfigManager.Format> priorityAndFormat : MessageOverHear.getConfigManager().getMessageFormat().entrySet()) {
            String perm = priorityAndFormat.getValue().getPermission();
            List<String> format = priorityAndFormat.getValue().getFormatsMessage();
            if (perm == null || player.hasPermission(perm)) {
                defaultFormat = format;
            }
        }
        return defaultFormat;
    }

    public boolean haveSendPermission(Player player) {
        if(getPermissionVault() != null) {
            return getPermissionVault().has(player, getPermSend());
        }
        else return player.hasPermission(getPermSend());
    }
    public boolean haveSeePermission(Player player) {
        if(getPermissionVault() != null) {
            return getPermissionVault().has(player, getPermSee());
        }
        else return player.hasPermission(getPermSee());
    }
}
