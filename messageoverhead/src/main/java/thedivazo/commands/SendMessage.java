
package thedivazo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import thedivazo.MessageOverHear;
import thedivazo.utils.BubbleMessageManager;

@CommandAlias("messageoverhear|moh")
public class SendMessage extends BaseCommand {

    @Subcommand("send")
    @CommandPermission("moh.send")
    @Syntax("<сообщение>")
    public void onCommand(Player player, String message) {
        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;
        if (!player.hasPermission(MessageOverHear.getConfigPlugin().getPermSend())) return;
        BubbleMessageManager bubbleMessageManager = new BubbleMessageManager(message, player);
        bubbleMessageManager.generateBubbleMessageInThread();
    }
}

