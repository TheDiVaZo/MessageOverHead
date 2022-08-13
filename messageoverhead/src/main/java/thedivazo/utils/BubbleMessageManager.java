package thedivazo.utils;

import de.myzelyam.api.vanish.VanishAPI;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.font.FontManager;
import io.th0rgal.oraxen.font.Glyph;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import thedivazo.MessageOverHear;

import java.util.*;
import java.util.regex.Pattern;


public class BubbleMessageManager {
    private String msg;
    private Player player;
    @Getter
    private static final HashMap<UUID, BubbleMessage> bubbleMessageMap = new HashMap<>();

    private BubbleMessage bubbleMessage;

    public BubbleMessageManager(String msg, Player player) {
        this.msg = msg;
        this.player = player;

        Location loc = player.getLocation();
        loc.setY(loc.getY() + MessageOverHear.getConfigPlugin().getBiasY());
    }

    public static void removeOtherBubble(OfflinePlayer player) {
        removeOtherBubble(player.getUniqueId());
    }

    public static List<String> convertMsgToLinesBubble(String msg) {
        msg = StringColor.ofText(msg);
        return StringUtil.insertsSymbol(StringUtil.stripsSymbol(msg, StringColor.CHAT_COLOR_PAT), StringUtil.splitText(ChatColor.stripColor(msg), MessageOverHear.getConfigPlugin().getSizeLine()));
    }

    public static void removeOtherBubble(UUID player) {
        if (BubbleMessageManager.getBubbleMessageMap().containsKey(player)) {
            bubbleMessageMap.get(player).remove();
            bubbleMessageMap.remove(player);
        }
    }

    public void removeBubble() {
        if (bubbleMessageMap.get(player.getUniqueId()).equals(bubbleMessage)) {
            bubbleMessageMap.remove(player.getUniqueId());
            bubbleMessage.remove();
        }
    }

    private String setEmoji(Player player, String text) {
        if(MessageOverHear.getConfigPlugin().isIALoaded()) text = FontImageWrapper.replaceFontImages(player, text);
        StringBuilder textBuilder = new StringBuilder(text);
        if(MessageOverHear.getConfigPlugin().isOraxenLoaded()) {
            FontManager fontManager = OraxenPlugin.get().getFontManager();
            for (Glyph glyph : fontManager.getEmojis()) {
                if(glyph.hasPermission(player)) {
                    Arrays.stream(glyph.getPlaceholders()).forEach(glyphPlaceholder->{
                        if (textBuilder.toString().contains(glyphPlaceholder)) {
                            textBuilder.replace(
                                    textBuilder.indexOf(glyphPlaceholder),
                                    textBuilder.lastIndexOf(glyphPlaceholder),
                                    glyph.getTexture());
                        }
                    });
                }
            }
        }
        return textBuilder.toString();
    }

    private String setPlaceholders(Player player, String text) {
        if(MessageOverHear.getConfigPlugin().isPAPILoaded()) text = PlaceholderAPI.setPlaceholders(player, text);
        return text;
    }

    public void generateBubbleMessageInThread() {
        MessageOverHear.service.submit(BubbleMessageManager.this::generateBubbleMessage);
    }

    public void generateBubbleMessage() {
        List<String> messageLines = new ArrayList<>();
        List<String> formatLines = getFormatOfPlayer(player);
        for(String format: formatLines) {
                messageLines.addAll(StringColor.ofText(convertMsgToLinesBubble(setEmoji(player, setPlaceholders(player, format).replace("%message%", StringColor.toNoColorString(msg))))));
        }

        if (bubbleMessageMap.containsKey(player.getUniqueId()))
            bubbleMessageMap.get(player.getUniqueId()).remove();

        bubbleMessage = new BubbleMessage(player, messageLines);

        bubbleMessageMap.put(player.getUniqueId(), bubbleMessage);

        spawnBubble();

        BukkitTask taskMove = new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = player.getLocation().clone();
                loc.setY(loc.getY() + MessageOverHear.getConfigPlugin().getBiasY());
                bubbleMessage.setPosition(loc);
            }
        }.runTaskTimerAsynchronously(MessageOverHear.getInstance(), 1L, 1L);

        BukkitTask taskDelete = new BukkitRunnable() {
            @Override
            public void run() {
                taskMove.cancel();
                removeBubble();
            }
        }.runTaskLaterAsynchronously(MessageOverHear.getInstance(), MessageOverHear.getConfigPlugin().getDelay() * 20L);
        bubbleMessage.removeTask(taskDelete, taskMove);
    }

    public void spawnBubble() {
        Bukkit.getOnlinePlayers().stream().filter(onlinePlayer -> {

            boolean canSeePlayer = onlinePlayer.canSee(player) && onlinePlayer.getWorld().equals(player.getWorld());
            boolean isNotInvisiblePlayer = canSeeSuperVanish(onlinePlayer, player) && !player.hasPotionEffect(PotionEffectType.INVISIBILITY);
            boolean isBeside = onlinePlayer.getLocation().distance(player.getLocation()) < MessageOverHear.getConfigPlugin().getDistance();

            return onlinePlayer.hasPermission("moh.see") && canSeePlayer && isBeside && isNotInvisiblePlayer;})

            .forEach(onlinePlayer -> {
                if (!onlinePlayer.equals(player) || !MessageOverHear.getConfigPlugin().isVisibleTextForOwner()) return;
                if (MessageOverHear.getConfigPlugin().isSoundEnable())
                    bubbleMessage.playSound(onlinePlayer);
                if (MessageOverHear.getConfigPlugin().isParticleEnable())
                    bubbleMessage.playParticle(onlinePlayer);
                bubbleMessage.show(onlinePlayer);
            });
    }

    public boolean canSeeSuperVanish(Player viewer, Player viewed) {
        if (MessageOverHear.getConfigPlugin().isSuperVanishLoaded()) {
            return VanishAPI.canSee(viewer, viewed);
        } else return true;
    }

    public List<String> getFormatOfPlayer(Player player) {
        Integer[] priorityFormat = MessageOverHear.getConfigPlugin().getMorePermissionFormat().keySet().toArray(new Integer[]{});
        Arrays.sort(priorityFormat);
        List<String> format = new ArrayList<>();
        format.add(MessageOverHear.getConfigPlugin().getOneDefaultMessageFormat());

        for (int priority : priorityFormat) {
            String permission = MessageOverHear.getConfigPlugin().getMorePermissionFormat().get(priority);
            List<String> maybeFormat = MessageOverHear.getConfigPlugin().getMoreMessageFormat().get(permission);
            if (permission != null) {
                if (player.hasPermission(permission)) format = maybeFormat;
            } else {
                format = maybeFormat;
            }
        }
        return format;
    }
}
