package thedivazo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import thedivazo.MessageOverHear;
import thedivazo.config.ConfigBubble;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@CommandAlias("messageoverhear|moh")
public class DefaultCommands extends BaseCommand {

    @Subcommand("send")
    @Syntax("<сообщение>")
    @CommandPermission("moh.send")
    @Description("Команда позваляет вызвать сообщение над головой без чата\n1 аргумент - само сообщение\n2 аргумент - название конфигурации бабла из конфига плагина. По умолчанию - \"messages\"\n3 - ники игроков через пробел. Это те, кому будет видно сообщение")
    public void onCommand(Player player, String message) {
        MessageOverHear.createBubbleMessage(MessageOverHear.getConfigManager().getConfigBubble("messages"), player.getPlayer(), message);
    }

    @Subcommand("create")
    @CommandPermission("moh.admin")
    @Syntax("<sender> <recipient recipients> <bubbleConfig> <message>")
    @CommandCompletion("@players @players @configBubbles сообщение")
    public void onCommand(CommandSender commandSender, OfflinePlayer sender, String recipients, String bubbleConfig, String messages) {
        Set<Player> recipientPlayers = Arrays.stream(recipients.split(",")).filter(p->!Objects.isNull(Bukkit.getPlayer(p))).map(Bukkit::getPlayer).collect(Collectors.toSet());
        ConfigBubble configBubble = MessageOverHear.getConfigManager().getConfigBubble(bubbleConfig);
        if(!sender.isOnline()) {
            commandSender.sendMessage("Игрок "+sender.getName()+" не в сети.");
        }
        else MessageOverHear.createBubbleMessage(configBubble, sender.getPlayer(), messages, recipientPlayers);
    }

    @Subcommand("reload")
    @CommandPermission("moh.admin")
    public void onCommand(CommandSender commandSender) {
        MessageOverHear.getInstance().reloadConfigManager();
        commandSender.sendMessage("Config has been reloaded");
    }
}
