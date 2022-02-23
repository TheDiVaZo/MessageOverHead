
package io.messageoverhead.thedivazo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin implements Listener, thisPlug {
    private static Main instance;
    private static volatile Map<UUID, BubbleMessage> BubbleChat = new HashMap<UUID, BubbleMessage>();

    public static boolean EnablePlaceholderAPI = false;
    public static boolean EnableProtocolLib = false;

    public static boolean ParticleEnable = true;
    public static Particle ParticleType = Particle.valueOf("CLOUD");
    public static int ParticleCount = 4;
    public static double ParticleOffsetX = 0.2;
    public static double ParticleOffsetY = 0.2;
    public static double ParticleOffsetZ = 0.2;

    public static boolean SoundEnable = true;
    public static Sound SoundType = Sound.valueOf("BLOCK_ANVIL_STEP");
    public static int SoundVolume = 4;
    public static int SoundPitch = 4;

    public static String Format = "%player_name% %message%";
    public static int Distance = 10;
    public static double BiasY = 2.15;
    public static boolean VisibleTextForOwner = false;
    public static String PermSend = "moh.send";
    public static String PermSee = "moh.see";

    public static int Delay = 4;

    @Override
    public void saveParam() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            EnablePlaceholderAPI = true;
        }
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            EnableProtocolLib = true;
        }
        FileConfiguration config = getConfig();
        ////////////
        ParticleEnable = config.getBoolean("messages.particle.enable");
        try {
            ParticleType = Particle.valueOf(config.getString("messages.particle.particleType"));
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Wrong particle type. Please check your config. Default particle type set: CLOUD");
        }
        ParticleCount = config.getInt("messages.particle.count");
        ParticleOffsetX = config.getDouble("message.particle.offsetX");
        ParticleOffsetY = config.getDouble("message.particle.offsetY");
        ParticleOffsetZ = config.getDouble("message.particle.offsetZ");
        /////////////
        SoundEnable = config.getBoolean("messages.sound.enable");
        try {
            SoundType = Sound.valueOf(config.getString("messages.sound.soundType"));
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Wrong sound type. Please check your config. Default sound type set: BLOCK_ANVIL_STEP");
        }
        SoundVolume = config.getInt("messages.sound.volume");
        SoundPitch = config.getInt("messages.sound.pitch");
        ///////////////
        Format = config.getString("messages.settings.format");
        Distance = config.getInt("messages.settings.distance");
        BiasY = config.getDouble("messages.settings.biasY");
        VisibleTextForOwner  = config.getBoolean("messages.settings.visibleTextForOwner");
        PermSend = config.getString("messages.settings.permSend");
        PermSee = config.getString("messages.settings.permSee");
        Delay = config.getInt("messages.settings.delay");

        Permission.loadPermission(PermSee, new HashMap<>(), PermissionDefault.NOT_OP, new ArrayList<Permission>());
        Permission.loadPermission(PermSend, new HashMap<>(), PermissionDefault.NOT_OP, new ArrayList<Permission>());

    }

    public void onEnable() {
        super.onEnable();
        this.saveDefaultConfig();
        this.saveParam();
        if(!EnableProtocolLib) {
            Bukkit.getLogger().warning("ProtocolLib not found! Please download the latest version of ProtocolLib");
            this.onDisable();
        }
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("moh").setExecutor(new reloadConfig(this));
        this.getCommand("messageoverhead").setExecutor(new reloadConfig(this));
        this.getServer().getConsoleSender().sendMessage(this.getServer().getVersion());
        instance = this;
    }

    public void onDisable() {
        super.onDisable();
        for (BubbleMessage message : BubbleChat.values()) {
            message.remove();
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if (BubbleChat.containsKey(e.getPlayer().getUniqueId())) {
            BubbleChat.get(e.getPlayer().getUniqueId()).remove();
            BubbleChat.remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent e) {
        if(!e.getPlayer().hasPermission(PermSend)) return;
        Location loc = e.getPlayer().getLocation();
        loc.setY(loc.getY()+BiasY);
        Bukkit.getConsoleSender().sendMessage(e.getMessage());

        String msg = e.getMessage();
        while(msg.contains("\\"))msg = msg.replace('\\', '/');

        String message;
        if(EnablePlaceholderAPI) message = makeColors(PlaceholderAPI.setPlaceholders(e.getPlayer(), Format)).replaceAll("%message%", msg);
        else message = makeColors(Format).replaceAll("%message%", msg);

        //Bukkit.getConsoleSender().sendMessage(message);
        BubbleMessage bubbleMessage = new BubbleMessage(message, loc);
        if(SoundEnable) bubbleMessage.sound(Distance);
        if(ParticleEnable) bubbleMessage.particle(Distance);

        if(BubbleChat.containsKey(e.getPlayer().getUniqueId())) BubbleChat.get(e.getPlayer().getUniqueId()).remove();

        BubbleChat.put(e.getPlayer().getUniqueId(), bubbleMessage);

        for(Player p: Bukkit.getOnlinePlayers()) {
            if(p.getLocation().distance(loc) < Distance) {
                if(!VisibleTextForOwner ? !p.equals(e.getPlayer()):true) {
                    if(p.hasPermission(PermSee)) bubbleMessage.spawn(p);
                }
            }
        }
        BukkitTask TaskMove = new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = e.getPlayer().getLocation().clone();
                loc.setY(loc.getY() + BiasY);
                bubbleMessage.setPosition(loc);
            }
        }.runTaskTimer(this, 1L, 1L);

        BukkitTask TaskDelete = new BukkitRunnable() {
            @Override
            public void run() {
                TaskMove.cancel();
                if(BubbleChat.get(e.getPlayer().getUniqueId()).equals(bubbleMessage)) {
                    BubbleChat.remove(e.getPlayer().getUniqueId());
                    bubbleMessage.remove();
                }
            }
        }.runTaskLater(this, Delay * 20);
        bubbleMessage.RemoveTask(TaskDelete, TaskMove);
    }

    public static Main getInstance() {
        return instance;
    }

    public final static Pattern HEXPAT = Pattern.compile("&#[a-fA-F0-9]{6}");
    public static String makeColors(String s){
        //Handle standard basic colors
        while(s.contains("&0"))s = s.replace("&0", ChatColor.BLACK + "");
        while(s.contains("&1"))s = s.replace("&1", ChatColor.DARK_BLUE + "");
        while(s.contains("&2"))s = s.replace("&2", ChatColor.DARK_GREEN + "");
        while(s.contains("&3"))s = s.replace("&3", ChatColor.DARK_AQUA + "");
        while(s.contains("&4"))s = s.replace("&4", ChatColor.DARK_RED + "");
        while(s.contains("&5"))s = s.replace("&5", ChatColor.DARK_PURPLE + "");
        while(s.contains("&6"))s = s.replace("&6", ChatColor.GOLD + "");
        while(s.contains("&7"))s = s.replace("&7", ChatColor.GRAY + "");
        while(s.contains("&8"))s = s.replace("&8", ChatColor.DARK_GRAY + "");
        while(s.contains("&9"))s = s.replace("&9", ChatColor.BLUE + "");
        while(s.contains("&a"))s = s.replace("&a", ChatColor.GREEN + "");
        while(s.contains("&b"))s = s.replace("&b", ChatColor.AQUA + "");
        while(s.contains("&c"))s = s.replace("&c", ChatColor.RED + "");
        while(s.contains("&d"))s = s.replace("&d", ChatColor.LIGHT_PURPLE + "");
        while(s.contains("&e"))s = s.replace("&e", ChatColor.YELLOW + "");
        while(s.contains("&f"))s = s.replace("&f", ChatColor.WHITE + "");
        while(s.contains("&k"))s = s.replace("&k", ChatColor.MAGIC + "");
        while(s.contains("&l"))s = s.replace("&l", ChatColor.BOLD + "");
        while(s.contains("&m"))s = s.replace("&m", ChatColor.STRIKETHROUGH + "");
        while(s.contains("&n"))s = s.replace("&n", ChatColor.UNDERLINE + "");
        while(s.contains("&o"))s = s.replace("&o", ChatColor.ITALIC + "");
        while(s.contains("&r"))s = s.replace("&r", ChatColor.RESET + "");
        if(Bukkit.getVersion().indexOf("1.16") == -1 && Bukkit.getVersion().indexOf("1.17") == -1 && Bukkit.getVersion().indexOf("1.18")==-1) return s;
        Matcher match = HEXPAT.matcher(s);
        while(match.find()) {
            String color = s.substring(match.start(), match.end());
            s = s.replace(color, ChatColor.of(color.replace("&", "")) + "");
        }

        return s;
    }

}

