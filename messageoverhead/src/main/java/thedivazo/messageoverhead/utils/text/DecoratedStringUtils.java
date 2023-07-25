package thedivazo.messageoverhead.utils.text;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import thedivazo.messageoverhead.utils.text.element.Chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DecoratedStringUtils {
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

    public static DecoratedString insertPlaceholders(DecoratedString text, OfflinePlayer player) {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) return text;
        return DecoratedString.builder().chunks(text.toChunksList().stream().map(chunk -> chunk.toBuilder().setText(PlaceholderAPI.setPlaceholders(player,chunk.getText())).build()).collect(Collectors.toList())).build();
    }

    public static DecoratedString wrapString(String str) {
        return DecoratedString.builder().chunks(new ArrayList<>(){{add(Chunk.builder().setText(str).build());}}).build();
    }
}
