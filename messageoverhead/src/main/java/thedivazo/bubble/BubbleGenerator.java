package thedivazo.bubble;

import org.bukkit.entity.Player;
import thedivazo.config.BubbleModel;

import java.util.List;

public class BubbleGenerator {
    private BubbleModel bubbleModel;

    private List<String> getPlayerFormat(Player player) {
        List<BubbleModel.FormatMessageModel> allFormatMessage = bubbleModel.getFormatMessageModels();

        for (BubbleModel.FormatMessageModel formatMessage : allFormatMessage) {
            if(player.isPermissionSet(formatMessage.getPermission())) return formatMessage.getLines();
        }
        return allFormatMessage.get(allFormatMessage.size()-1).getLines();
    }

    private List<String> insertPlayerText(Player player, String text) {
        return null;
    }

    public BubbleWrapper generateBubble(Player ownerBubble, String text) {
        return null;
    }

    public boolean isPermission(Player player) {
        return player.isPermissionSet(bubbleModel.getPermission());
    }
}
