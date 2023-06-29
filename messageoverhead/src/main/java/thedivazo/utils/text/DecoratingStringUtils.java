package thedivazo.utils.text;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class DecoratingStringUtils {
    public static List<DecoratedString> splitText(DecoratedString text, int maxSizeLine, boolean cutWorlds) {
        return null;
    }

    public static String setPlaceholders(Player player, String text) {
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) return PlaceholderAPI.setPlaceholders(player, text);
        else return text;
    }
}
