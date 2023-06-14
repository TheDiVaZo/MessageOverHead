package thedivazo.utils.text;

import com.comphenix.protocol.PacketType;
import dev.lone.itemsadder.api.ItemsAdder;
import io.th0rgal.oraxen.OraxenPlugin;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class TextDecoratorUtil {
    public static List<DecoratedString> splitText(DecoratedString text, int maxSizeLine, boolean cutWorlds) {
        return null;
    }

    public static String setPlaceholders(Player player, String text) {
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) return PlaceholderAPI.setPlaceholders(player, text);
        else return text;
    }
}
