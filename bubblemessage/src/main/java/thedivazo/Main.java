
package thedivazo;

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
import org.bukkit.event.player.PlayerQuitEvent;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
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
@ApiVersion(ApiVersion.Target.v1_14)
public class Main extends JavaPlugin implements Listener {

    // Паттерн проектирования - Синглтон - хрень. Да, простое, но не все так просто.
    // Можешь прочитать тут подробнее: https://refactoring.guru/ru/design-patterns/catalog

    // Прочитай разницу между HashMap & Map и реши сам, что тебе нужнее
    // Есть Java конвенция, прочитай краткое описание <!>
    //   вкратце: название классов - с Большой буквы
    //            + название переменных с маленькой буквы в camelCase
    private final HashMap<UUID, BubbleMessage> bubbleMessageMap = new HashMap<>();

    public boolean isPAPILoaded = false;

    public boolean particleEnable = true;

    // Ты можешь напрямую назначать переменную ENUM, а не через valueOf
    public Particle particleType = Particle.CLOUD;
    public int particleCount = 4;
    public double particleOffsetX = 0.2;
    public double particleOffsetY = 0.2;
    public double particleOffsetZ = 0.2;

    public boolean soundEnable = true;
    public Sound soundType = Sound.BLOCK_ANVIL_STEP;
    public int soundVolume = 4;
    public int soundPitch = 4;

    public String sormat = "%player_name% %message%";
    public int distance = 10;
    public double biasY = 2.15;

    // boolean переменные, как правило, начинаются с "is", а дальше ее назначение.
    public boolean isVisibleTextForOwner = false;
    public String permSend = "moh.send";
    public String permSee = "moh.see";

    public int delay = 4;


    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.saveParam();
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("moh").setExecutor(new ReloadConfig(this));
        this.getCommand("messageoverhead").setExecutor(new ReloadConfig(this));
        this.getServer().getConsoleSender().sendMessage(this.getServer().getVersion());
    }

    public void saveParam() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            isPAPILoaded = true;
        }
        FileConfiguration config = getConfig();

        // Если у тебя партиклы отключены, то нам не стоит дальше выполнять код
        //   и "тратить" на это время т.к остальные параметры нигде и никогда не будут
        //   использоваться т.к партиклы отключены.
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

        // Аналогично как выше
        if (config.getBoolean("messages.sound.enable")) {
            try {
                soundType = Sound.valueOf(config.getString("messages.sound.soundType"));
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("Wrong sound type. Please check your config. Default sound type set: BLOCK_ANVIL_STEP");
            }
            soundVolume = config.getInt("messages.sound.volume");
            soundPitch = config.getInt("messages.sound.pitch");
        }
        ///////////////
        sormat = config.getString("messages.settings.format");
        distance = config.getInt("messages.settings.distance");
        biasY = config.getDouble("messages.settings.biasY");
        isVisibleTextForOwner = config.getBoolean("messages.settings.visibleTextForOwner");
        permSend = config.getString("messages.settings.permSend");
        permSee = config.getString("messages.settings.permSee");
        delay = config.getInt("messages.settings.delay");
    }

    @Override
    public void onDisable() {
        // Фишки Java 8 (лямбда выражения) удобные, читаемые и функциональные
        bubbleMessageMap.values().forEach(BubbleMessage::remove);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        // Читабельнее
        UUID playerUUID = e.getPlayer().getUniqueId();
        if (bubbleMessageMap.containsKey(playerUUID)) {
            bubbleMessageMap.get(playerUUID).remove();
            bubbleMessageMap.remove(playerUUID);
        }
    }

    // А ты знаешь что означает этот приоритет?
    @EventHandler(priority = EventPriority.MONITOR)
    // Переменная названа "е" - не информативно. Лучше назвать event, это хотя бы что-то значит
    // Название метода onChat, я считаю, не верным. Тут подойдет onPlayerChat т.к тут слушается
    //   только сообщения игроков в чат, никакие другие.
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        // Создав эту переменную ты уменьшил количество символов в переменных
        //  но от этго смысл не потерялся. Стало чище и понятнее.
        Player player = e.getPlayer();

        if (!player.hasPermission(permSend)) return;

        Location loc = player.getLocation();

        loc.setY(loc.getY() + biasY);

        // Для чего это нужно? Типа debug сообщение? Почему оно попало в финальный вариант?
        Bukkit.getConsoleSender().sendMessage(e.getMessage());

        String msg = e.getMessage();
        while (msg.contains("\\")) msg = msg.replace('\\', '/');

        String message;
        if (isPAPILoaded)
            message = makeColors(PlaceholderAPI.setPlaceholders(player, sormat)).replaceAll("%message%", msg);
        else
            message = makeColors(sormat).replaceAll("%message%", msg);

        // А тут ты закомментировал :)
        //Bukkit.getConsoleSender().sendMessage(message);

        BubbleMessage bubbleMessage = new BubbleMessage(message, loc);

        // Делать много пробелов - плохо, но без них код сливается и плохо читается
        if (soundEnable)
            bubbleMessage.sound(distance);

        if (particleEnable)
            bubbleMessage.particle(distance);

        if (bubbleMessageMap.containsKey(player.getUniqueId()))
            bubbleMessageMap.get(player.getUniqueId()).remove();

        bubbleMessageMap.put(player.getUniqueId(), bubbleMessage);

        // выглядит лучше, согласен?
        // Переменная "p" - не информативная
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getLocation().distance(loc) < distance) {
                // увидел что поменялось? Нет - гляди свой старый код.
                if (isVisibleTextForOwner || !onlinePlayer.equals(player)) {
                    if (onlinePlayer.hasPermission(permSee))
                        bubbleMessage.spawn(onlinePlayer);
                }
            }
        }

        // camelCase, не забывай
        BukkitTask taskMove = new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = player.getLocation().clone();
                loc.setY(loc.getY() + biasY);
                bubbleMessage.setPosition(loc);
            }
        }.runTaskTimer(this, 1L, 1L);

        BukkitTask taskDelete = new BukkitRunnable() {
            @Override
            public void run() {
                taskMove.cancel();
                if (bubbleMessageMap.get(player.getUniqueId()).equals(bubbleMessage)) {
                    bubbleMessageMap.remove(player.getUniqueId());
                    bubbleMessage.remove();
                }
            }
            // тут лучше указать явный тип Long через "L" в конце числа
        }.runTaskLater(this, delay * 20L);
        bubbleMessage.removeTask(taskDelete, taskMove);
    }

    public final static Pattern HEXPAT = Pattern.compile("&#[a-fA-F0-9]{6}");

    // Это ужас с этими бесконечными while. Найди вариант лучше, я даже не смотрел дальше.
    public static String makeColors(String s) {
        //Handle standard basic colors
        while (s.contains("&0")) s = s.replace("&0", ChatColor.BLACK + "");
        while (s.contains("&1")) s = s.replace("&1", ChatColor.DARK_BLUE + "");
        while (s.contains("&2")) s = s.replace("&2", ChatColor.DARK_GREEN + "");
        while (s.contains("&3")) s = s.replace("&3", ChatColor.DARK_AQUA + "");
        while (s.contains("&4")) s = s.replace("&4", ChatColor.DARK_RED + "");
        while (s.contains("&5")) s = s.replace("&5", ChatColor.DARK_PURPLE + "");
        while (s.contains("&6")) s = s.replace("&6", ChatColor.GOLD + "");
        while (s.contains("&7")) s = s.replace("&7", ChatColor.GRAY + "");
        while (s.contains("&8")) s = s.replace("&8", ChatColor.DARK_GRAY + "");
        while (s.contains("&9")) s = s.replace("&9", ChatColor.BLUE + "");
        while (s.contains("&a")) s = s.replace("&a", ChatColor.GREEN + "");
        while (s.contains("&b")) s = s.replace("&b", ChatColor.AQUA + "");
        while (s.contains("&c")) s = s.replace("&c", ChatColor.RED + "");
        while (s.contains("&d")) s = s.replace("&d", ChatColor.LIGHT_PURPLE + "");
        while (s.contains("&e")) s = s.replace("&e", ChatColor.YELLOW + "");
        while (s.contains("&f")) s = s.replace("&f", ChatColor.WHITE + "");
        while (s.contains("&k")) s = s.replace("&k", ChatColor.MAGIC + "");
        while (s.contains("&l")) s = s.replace("&l", ChatColor.BOLD + "");
        while (s.contains("&m")) s = s.replace("&m", ChatColor.STRIKETHROUGH + "");
        while (s.contains("&n")) s = s.replace("&n", ChatColor.UNDERLINE + "");
        while (s.contains("&o")) s = s.replace("&o", ChatColor.ITALIC + "");
        while (s.contains("&r")) s = s.replace("&r", ChatColor.RESET + "");
        if (Bukkit.getVersion().indexOf("1.16") == -1 && Bukkit.getVersion().indexOf("1.17") == -1 && Bukkit.getVersion().indexOf("1.18") == -1)
            return s;
        Matcher match = HEXPAT.matcher(s);
        while (match.find()) {
            String color = s.substring(match.start(), match.end());
            s = s.replace(color, ChatColor.of(color.replace("&", "")) + "");
        }

        return s;
    }

}

