package me.thedivazo.messageoverhead.command;

import me.thedivazo.messageoverhead.core.BubbleManager;
import me.thedivazo.messageoverhead.core.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;

import java.util.Objects;

public final class BubbleTestCommand {
    private static final CloudKey<String> TEXT = CloudKey.of("text", String.class);

    private BubbleTestCommand() {
    }

    public static void register(
            LegacyPaperCommandManager<CommandSender> commandManager,
            BubbleManager bubbleManager
    ) {
        Objects.requireNonNull(commandManager, "commandManager");
        Objects.requireNonNull(bubbleManager, "bubbleManager");

        commandManager.command(
                commandManager.commandBuilder("bubble-test")
                        .senderType(Player.class)
                        .required(TEXT, StringParser.greedyStringParser())
                        .handler(context -> {
                            Player player = context.sender();
                            String text = context.optional(TEXT).orElseThrow();
                            PlayerBubbleAuthor author = new PlayerBubbleAuthor(player);

                            bubbleManager.spawnBubble(
                                    new Message(Component.text(text)),
                                    author,
                                    author.getPosition()
                            );
                        })
        );
    }
}
