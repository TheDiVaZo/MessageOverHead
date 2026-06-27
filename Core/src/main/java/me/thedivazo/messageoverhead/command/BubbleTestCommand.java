package me.thedivazo.messageoverhead.command;

import me.thedivazo.messageoverhead.api.ProfileService;
import me.thedivazo.messageoverhead.api.SpawnService;
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
            SpawnService spawnService,
            ProfileService profileService
    ) {
        Objects.requireNonNull(commandManager, "commandManager");
        Objects.requireNonNull(spawnService, "spawnService");
        Objects.requireNonNull(profileService, "profileService");

        commandManager.command(
                commandManager.commandBuilder("bubble-test")
                        .senderType(Player.class)
                        .required(TEXT, StringParser.greedyStringParser())
                        .handler(context -> {
                            Player player = context.sender();
                            String text = context.optional(TEXT).orElseThrow();
                            PlayerAuthor author = new PlayerAuthor(player);

                            spawnService.spawn(
                                    new Message(Component.text(text)),
                                    author,
                                    profileService.defaultProfile()
                            );
                        })
        );
    }
}
