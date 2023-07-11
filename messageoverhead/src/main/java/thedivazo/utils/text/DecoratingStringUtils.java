package thedivazo.utils.text;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DecoratingStringUtils {
    public static List<DecoratedString> splitText(DecoratedString text, int maxSizeLine) {
        List<DecoratedString> result = new ArrayList<>();
        int currentIndex = 0;
        int prevSpaceIndex = -1;
        for (int i = maxSizeLine;currentIndex < text.length(); i+=maxSizeLine) {
            int currentSpaceIndex = text.lastIndexOf(" ", Math.min(currentIndex+maxSizeLine, text.length()));
            if (currentSpaceIndex == prevSpaceIndex) {
                result.add(text.subDecoratedString(currentIndex, Math.min(currentIndex+maxSizeLine, text.length())));
                currentIndex = Math.min(currentIndex+maxSizeLine, text.length());
            }
            else {
                prevSpaceIndex = currentSpaceIndex;
                result.add(text.subDecoratedString(currentIndex, currentSpaceIndex));
                currentIndex += currentSpaceIndex - currentIndex;
                //skip space
                currentIndex += 1;
            }

            if (i>text.length()) break;
        }
        return result;
    }

    public static String setPlaceholders(Player player, String text) {
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) return PlaceholderAPI.setPlaceholders(player, text);
        else return text;
    }
}
