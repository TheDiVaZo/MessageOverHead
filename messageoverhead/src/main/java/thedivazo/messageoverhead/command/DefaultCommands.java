package thedivazo.messageoverhead.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;
import thedivazo.messageoverhead.BubbleActiveStatus;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.bubble.BubbleManager;
import thedivazo.messageoverhead.bubble.BubbleSpawned;

@CommandAlias("moh")
public class DefaultCommands extends BaseCommand {
    @Subcommand("enable")
    @CommandPermission("moh.enable")
    public static void onEnable(Player player) {
        BubbleActiveStatus.setStatus(player, BubbleActiveStatus.Status.ENABLED);
        BubbleManager bubbleManager = MessageOverHead.getConfigManager().getBubbleManager();
        bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::show);
    }

    @Subcommand("disable")
    @CommandPermission("moh.disable")
    public static void onDisable(Player player) {
        BubbleActiveStatus.setStatus(player, BubbleActiveStatus.Status.DISABLED);
        BubbleManager bubbleManager = MessageOverHead.getConfigManager().getBubbleManager();
        bubbleManager.getBubbleSpawned(player).ifPresent(BubbleSpawned::hide);
    }
}
