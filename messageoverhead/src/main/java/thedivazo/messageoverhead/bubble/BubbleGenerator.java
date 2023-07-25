package thedivazo.messageoverhead.bubble;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import thedivazo.messageoverhead.config.BubbleModel;
import thedivazo.messageoverhead.config.channel.Channel;
import thedivazo.messageoverhead.config.channel.Type;
import thedivazo.messageoverhead.utils.text.DecoratedString;
import thedivazo.messageoverhead.utils.text.DecoratedStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class BubbleGenerator {
    @Getter
    private BubbleModel bubbleModel;

    private List<DecoratedString> getPlayerFormat(Player player) {
        List<BubbleModel.FormatMessageModel> allFormatMessage = bubbleModel.getFormatMessageModels();

        for (BubbleModel.FormatMessageModel formatMessage : allFormatMessage) {
            if(Objects.isNull(formatMessage.getPermission()) || player.hasPermission(formatMessage.getPermission())) return formatMessage.getLines();
        }
        return allFormatMessage.get(allFormatMessage.size()-1).getLines();
    }

    private DecoratedString insertPlaceholders(DecoratedString text, OfflinePlayer player) {
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
        return new BubbleWrapper(ownerBubble.getLocation(), bubbleModel, processText(text, ownerBubble));
    }

    public String getName() {
        return bubbleModel.getName();
    }

    public boolean isPermission(Player player) {
        return Objects.isNull(bubbleModel.getPermission()) || player.hasPermission(bubbleModel.getPermission());
    }

    public boolean isChannel(Channel channel) {
        return bubbleModel.getChannels().stream().anyMatch(otherChannel->otherChannel.getType().equals(Type.ALL) || otherChannel.equals(channel));
    }
}
