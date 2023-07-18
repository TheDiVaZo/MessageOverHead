package thedivazo.bubble;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import thedivazo.config.BubbleModel;
import thedivazo.utils.text.DecoratedString;
import thedivazo.utils.text.DecoratedStringUtils;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class BubbleGenerator {
    private BubbleModel bubbleModel;

    private List<DecoratedString> getPlayerFormat(Player player) {
        List<BubbleModel.FormatMessageModel> allFormatMessage = bubbleModel.getFormatMessageModels();

        for (BubbleModel.FormatMessageModel formatMessage : allFormatMessage) {
            if(player.isPermissionSet(formatMessage.getPermission())) return formatMessage.getLines();
        }
        return allFormatMessage.get(allFormatMessage.size()-1).getLines();
    }

    private DecoratedString insertPlaceholders(DecoratedString text, Player player) {
        DecoratedString result = text;
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            result = DecoratedStringUtils.insertPlaceholders(result, player);
        }
        return result;
    }

    private List<DecoratedString> processText(DecoratedString text, Player player) {
        List<DecoratedString> format = getPlayerFormat(player);
        List<DecoratedString> result = new ArrayList<>();

        for (DecoratedString line : format) {
            result.addAll(DecoratedStringUtils.splitText(insertPlaceholders(line, player).replace("{message}",text), bubbleModel.getMaxSizeLine()));
        }
        return result;
    }

    public BubbleWrapper generateBubble(DecoratedString text, Player ownerBubble) {
        return new BubbleWrapper(ownerBubble.getLocation(), processText(text, ownerBubble));
    }

    public String getName() {
        return bubbleModel.getName();
    }

    public boolean isPermission(Player player) {
        return player.isPermissionSet(bubbleModel.getPermission());
    }
}
