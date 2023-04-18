package thedivazo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import thedivazo.MessageOverHead;
import thedivazo.config.ConfigBubble;

import java.util.*;
import java.util.stream.Collectors;

@CommandAlias("messageoverhear|moh")
public class DefaultCommands extends BaseCommand {

    @Subcommand("send")
    @Syntax("<сообщение>")
    @CommandPermission("moh.send")
    @Description("Команда позваляет вызвать сообщение над головой без чата\n1 аргумент - само сообщение\n2 аргумент - название конфигурации бабла из конфига плагина. По умолчанию - \"messages\"\n3 - ники игроков через пробел. Это те, кому будет видно сообщение")
    public void onCommand(Player player, String message) {
        MessageOverHead.createBubbleMessage(MessageOverHead.getConfigManager().getConfigBubble("messages"), player.getPlayer(), message);
    }

    @Subcommand("create")
    @CommandPermission("moh.admin")
    @Syntax("<sender> <recipient recipients> <bubbleConfig> <message>")
    @CommandCompletion("@players @players @configBubbles сообщение")
    public void onCommand(CommandSender commandSender, OfflinePlayer sender, String recipients, String bubbleConfig, String messages) {
        Set<Player> recipientPlayers = Arrays.stream(recipients.split(",")).filter(p->!Objects.isNull(Bukkit.getPlayer(p))).map(Bukkit::getPlayer).collect(Collectors.toSet());
        ConfigBubble configBubble = MessageOverHead.getConfigManager().getConfigBubble(bubbleConfig);
        if(!sender.isOnline()) {
            commandSender.sendMessage("Игрок "+sender.getName()+" не в сети.");
        }
        else MessageOverHead.createBubbleMessage(configBubble, sender.getPlayer(), messages, recipientPlayers);
    }

    @Subcommand("reload")
    @CommandPermission("moh.admin")
    public void onCommand(CommandSender commandSender) {
        MessageOverHead.getInstance().reloadConfigManager();
        commandSender.sendMessage("Config has been reloaded");
    }
}
