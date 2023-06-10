package thedivazo.manager.bubble;

import io.th0rgal.oraxen.shaded.jetbrains.annotations.Unmodifiable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import thedivazo.bubble.BubbleWrapper;
import thedivazo.manager.config.BubbleModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class BubbleMessage {
    @Getter
    private final BubbleModel bubbleModel;

    @Getter
    private final String message;

    public boolean isPermission(Player player) {
        return player.isPermissionSet(bubbleModel.getPermission());
    }

    public List<String> getFormatMessage(Player player) {
        return null;
        /*
        @Unmodifiable List<BubbleModel.FormatMessageModel> formatMessageModels = bubbleModel.getFormatMessageModels();
        for (int i = 0; i < formatMessageModels.size(); i++) {
            BubbleModel.FormatMessageModel formatMessageModel = formatMessageModels.get(i);
            if (player.isPermissionSet(formatMessageModel.getPermission()) || i+1 >= formatMessageModels.size()) {
                return formatMessageModel
                        .getLines()
                        .stream()
                        .map(line -> TextColorProcessor.setEmoji(player, TextColorProcessor.setPlaceholders(player, line))).collect(Collectors.toList());

            }
        }
        return new ArrayList<>();
         */
    }

    private List<String> getMessageLines(Player player) {
        //String modifyMessage = TextColorProcessor.cutColor(TextColorProcessor.setEmoji(player, message));
        List<String> formatMessage = getFormatMessage(player);
        List<String> messageLines = new ArrayList<>();
        for (String formatLine : formatMessage) {
            //List<String> preparedMessageLines = TextColorProcessor.chunkText(formatLine.replace("{message}", modifyMessage), bubbleModel.getMaxSizeLine(), true, false);
            //messageLines.addAll(preparedMessageLines);
        }
        return messageLines;
    }

    public BubbleWrapper generateBubbles(Player player, Location loc) {
        return new BubbleWrapper(loc, getMessageLines(player));
    }


}
