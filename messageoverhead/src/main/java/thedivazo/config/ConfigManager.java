package thedivazo.config;

import api.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import thedivazo.MessageOverHear;
import thedivazo.listener.chatlistener.*;
import thedivazo.manager.vanish.*;
import thedivazo.utils.ConfigUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getServer;

public class ConfigManager {

    public static final ImmutableConfigBubble defaultConfigBubble = new ImmutableConfigBubble();

    HashMap<String, ImmutableConfigBubble> configBubbles = new HashMap<>();

    @Getter
    private final MessageOverHear plugin;
    private FileConfiguration fileConfig;
    @Getter
    private ConfigUtils configUtils;

    @Getter
    private boolean isPAPILoaded = false;
    @Getter
    private boolean isIALoaded = false; //IA - ItemsAdder
    @Getter
    private boolean isOraxenLoaded = false;
    @Getter
    private boolean isVault = false;


    @Getter
    private boolean isInitVanishManager;
    @Getter
    private boolean isInitChatEventListener;

    @Getter
    private Permission permissionVault = null;

    @Getter
    private Set<ListenerWrapper> additionalListeners = new HashSet<>();

    @Getter
    @Setter
    private ChatListener<?, ?> chatEventListener;
    @Getter
    @Setter
    private VanishManager vanishManager;

    @Getter
    private boolean isEnableChatListener = true;
    @Getter
    private boolean isClearColorFromMessage = false;

    public ConfigManager(MessageOverHear plugin) {
        this.plugin = plugin;
        this.fileConfig = plugin.getConfig();
        this.configUtils = new ConfigUtils(fileConfig);
        plugin.saveDefaultConfig();
        updateConfig();
        reloadConfigFile();
    }

    public List<String> getConfigBubblesName() {
        return new ArrayList<>(configBubbles.keySet());
    }

    private boolean isPlugin(String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName) != null;
    }

    private void saveChatEventListener() {
        if(isInitChatEventListener) return;
        if (isPlugin("ChatControlRed")) {
            this.setChatEventListener(new ChatControlListener());
        }
        else this.setChatEventListener(new DefaultChatListener());
        isInitChatEventListener = true;
    }

    private void saveVanishManager() {
        if(isInitVanishManager) return;
        if (isPlugin("SuperVanish") || isPlugin("PremiumVanish")) {
            this.setVanishManager(new SuperVansihManager());
        }
        else if (isPlugin("Essentials")) {
            this.setVanishManager(new EssentialsVanishManager());
        }
        else if (isPlugin("CMI")) {
            this.setVanishManager(new CMIVanishManager());
        }
        else this.setVanishManager(new DefaultVanishManager());
        isInitVanishManager = true;
    }

    private void saveSoftDependCondition() {
        if (!isPlugin("ProtocolLib")) {
            Logger.warn("ProtocolLib not found! Please install ProtocolLib");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
        if (isPlugin("ItemsAdder")) {
            this.isIALoaded = true;
        }
        if (isPlugin("Oraxen")) {
            this.isOraxenLoaded = true;
        }
        if(isPlugin("Vault")) {
            this.isVault = true;
            RegisteredServiceProvider<Permission> rsp1 = getServer().getServicesManager().getRegistration(Permission.class);
            if(rsp1!= null) {
                permissionVault = rsp1.getProvider();
            }
        }
        if(isPlugin("PlaceholderAPI")) {
            this.isPAPILoaded = true;
        }
    }

    public void saveSettingsPlugin() {
        try {
            ConfigurationSection settings = fileConfig.getConfigurationSection("settings");
            ConfigurationSection listener = settings.getConfigurationSection("listener");
            isEnableChatListener = listener.getBoolean("chat");

            ConfigurationSection message = settings.getConfigurationSection("message");
            isClearColorFromMessage = message.getBoolean("clearColorFromPlayerMessage");
        } catch (Throwable e) {
            Logger.warn("Ошибка обновления настроек плагина! Пожалуйста, проверьте конфиг на наличие опечаток!");
            throw e;
        }
    }

    public boolean isConfigBubble(String path) {
        return configBubbles.containsKey(path);
    }

    public ImmutableConfigBubble getConfigBubble(String path) {
        ImmutableConfigBubble configBubble = configBubbles.get(path);
        if(Objects.isNull(configBubble)) {
            Logger.warn("Конфигурация "+path+" не найдена. Пожалуйста, проверьте конфиг на наличие опечаток.");
            configBubble = defaultConfigBubble;
        }
        return configBubble;
    }

    protected void generateConfigBubbles() {
        configBubbles.clear();
        for (String key : fileConfig.getKeys(false).stream().filter(k -> fileConfig.getBoolean(k + ".isBubble", false)).collect(Collectors.toSet())) {
            ConfigurationSection configBubbleSettings = fileConfig.getConfigurationSection(key);
            configBubbles.put(key, new ImmutableConfigBubble(configBubbleSettings));

        }
    }

    public void reloadConfigFile() {
        this.fileConfig = plugin.getConfig();
        this.configUtils.setConfig(fileConfig);
        getAdditionalListeners().clear();
        saveSoftDependCondition();
        saveVanishManager();
        saveSettingsPlugin();
        generateConfigBubbles();
        saveChatEventListener();
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
        //todo: write method
    }

    public String getConfigString() {
        return fileConfig.saveToString();
    }
}
