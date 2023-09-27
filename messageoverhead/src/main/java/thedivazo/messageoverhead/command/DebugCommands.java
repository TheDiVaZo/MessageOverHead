package thedivazo.messageoverhead.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.bubble.BubbleGenerator;
import thedivazo.messageoverhead.bubble.BubbleGeneratorManager;
import thedivazo.messageoverhead.channel.Channel;
import thedivazo.messageoverhead.channel.ChannelFactory;

import java.util.Objects;

@CommandAlias("dmoh")
public class DebugCommands extends BaseCommand {
    @Subcommand("getLoadedConfig")
    @CommandPermission("dmoh.command.getloadedconfig")
    public static void onGetLoadedConfig(CommandSender commandSender) {
        commandSender.sendMessage(
                ChatColor.RED
                + "Current loaded config:\n" +
                ChatColor.YELLOW
                + MessageOverHead.getInstance().getConfig().saveToString());
    }

    @Subcommand("getAccessBubbleModels")
    @CommandPermission("dmoh.command.getAccessBubbleModels")
    @CommandCompletion("@players")
    public static void onGetAccessBubbleModels(CommandSender commandSender, @Optional Player player, @Optional Channel channel) {

        if (Objects.nonNull(player)) {
            java.util.Optional<BubbleGenerator> bubbleGeneratorManager = MessageOverHead.getBubbleManager()
                    .getBubbleGeneratorManager()
                    .getBubbleGenerator(player, channel == null ? ChannelFactory.create("#all") : channel);
            if (bubbleGeneratorManager.isPresent()) {
                String result =
                        ChatColor.RED
                        + "Bubble Model name for "
                        + ChatColor.YELLOW
                        +  player.getName()
                        + "\n"+ChatColor.WHITE + bubbleGeneratorManager.get().getName();
                commandSender.sendMessage(result);
            }
            else commandSender.sendMessage(ChatColor.RED + "Bubble Model not found");
        }
        else  {
            StringBuilder stringBuilder = new StringBuilder(ChatColor.RED + "All Bubble Models:"+ChatColor.WHITE);
            MessageOverHead.getConfigAdapter().getBubbleModels().forEach(bubbleModel ->  stringBuilder.append("\n").append(bubbleModel.getName()));
            commandSender.sendMessage(stringBuilder.toString());
        }
    }
}
