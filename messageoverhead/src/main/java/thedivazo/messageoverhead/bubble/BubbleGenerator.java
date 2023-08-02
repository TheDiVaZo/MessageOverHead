package thedivazo.messageoverhead.bubble;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.api.event.BubbleSendEvent;
import thedivazo.messageoverhead.config.BubbleModel;
import thedivazo.messageoverhead.channel.Channel;
import thedivazo.messageoverhead.util.text.DecoratedString;
import thedivazo.messageoverhead.util.text.DecoratedStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    private List<DecoratedString> processText(String playerText, Player player) {
        List<DecoratedString> format = getPlayerFormat(player);
        List<DecoratedString> result = new ArrayList<>();
        for (DecoratedString line : format) {
            line = DecoratedStringUtils.insertPlaceholders(line, player).replace("{message}",playerText);
            List<DecoratedString> splintedText = DecoratedStringUtils.splitText(line, bubbleModel.getMaxSizeLine());
            result.addAll(splintedText);
        }
        return result;
    }

    private BubbleWrapper generateBubble(String playerText, Player ownerBubble) {
        return new BubbleWrapper(ownerBubble.getLocation(), processText(playerText, ownerBubble));
    }

    public BubbleSpawned spawnBubble(String playerText, Player sender, Set<Player> showers) {
        BubbleWrapper bubbleWrapper = generateBubble(playerText, sender);
        BubbleSpawned bubbleSpawned = new BubbleSpawned(bubbleModel, bubbleWrapper, sender, showers);
        callEvent(bubbleSpawned, sender);
        return bubbleSpawned;
    }

    private void callEvent(BubbleSpawned bubbleSpawned, Player sender) {
        Bukkit.getScheduler()
                .runTask(MessageOverHead.getInstance(), ()-> Bukkit.getPluginManager().callEvent(new BubbleSendEvent(bubbleSpawned, sender)));
    }

    public String getName() {
        return bubbleModel.getName();
    }

    public boolean isPermission(Player player) {
        return Objects.isNull(bubbleModel.getPermission()) || player.hasPermission(bubbleModel.getPermission());
    }

    public boolean isChannel(Channel channel) {
        return bubbleModel.getChannels().stream().anyMatch(channel::compatibilityChannel);
    }
}
