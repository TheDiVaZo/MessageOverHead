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

    public static List<DecoratedString> splitText(DecoratedString text, int maxSize) {
        List<DecoratedString> result = new ArrayList<>();

        while (text.length() > maxSize) {
            int endIndex = maxSize;

            while (endIndex > 0 && !Character.isWhitespace(text.charAt(endIndex))) {
                endIndex--;
            }

            if (endIndex == 0) {
                endIndex = maxSize;
            }

            result.add(text.subDecoratedString(0, endIndex));
            text = text.subDecoratedString(endIndex);
        }

        if (!text.isEmpty()) {
            result.add(text);
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
