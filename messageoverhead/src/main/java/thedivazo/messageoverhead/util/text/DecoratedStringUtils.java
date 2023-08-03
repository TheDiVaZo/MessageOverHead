package thedivazo.messageoverhead.util.text;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import thedivazo.messageoverhead.integration.IntegrationManager;

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

    public static DecoratedString insertPlaceholders(DecoratedString text, Player player) {
        if (!IntegrationManager.isPlaceholderAPI()) return text;
        return DecoratedString.builder()
                .chunks(
                        text.toChunksList()
                                .stream()
                                .map(chunk ->
                                        chunk.toBuilder().setText(PlaceholderAPI.setPlaceholders(player,insertItemsAdderPlaceholders(chunk.getText(), player))).build()).collect(Collectors.toList()))
                .build();
    }

    private static String insertItemsAdderPlaceholders(String text, Player player) {
        if (!IntegrationManager.isItemsAdder()) return text;
        return FontImageWrapper.replaceFontImages(player, text);
    }

    public static DecoratedString wrapString(String str) {
        return DecoratedString.builder().chunks(new ArrayList<>(){{add(Chunk.builder().setText(str).build());}}).build();
    }
}
