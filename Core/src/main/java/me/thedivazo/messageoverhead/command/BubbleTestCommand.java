package me.thedivazo.messageoverhead.command;

import me.thedivazo.messageoverhead.MessageOverHeadPlugin;
import me.thedivazo.messageoverhead.armorstand.ArmorStandBubbleFactory;
import me.thedivazo.messageoverhead.core.DefaultBubbleFactory;
import me.thedivazo.messageoverhead.core.component.ComponentRegistry;
import me.thedivazo.messageoverhead.core.component.PositionComponent;
import me.thedivazo.messageoverhead.core.component.ViewComponent;
import me.thedivazo.messageoverhead.core.component.scope.OffsetComponentScoped;
import me.thedivazo.messageoverhead.profile.BubbleSpawnManager;
import me.thedivazo.messageoverhead.core.Message;
import me.thedivazo.messageoverhead.profile.BubbleProfile;
import me.thedivazo.messageoverhead.profile.BubbleProfileImpl;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class BubbleTestCommand {
    private static final CloudKey<String> TEXT = CloudKey.of("text", String.class);

    private BubbleTestCommand() {
    }

    public static void register(
            LegacyPaperCommandManager<CommandSender> commandManager,
            BubbleSpawnManager bubbleSpawnManager,
            ComponentRegistry componentRegistry
    ) {
        Objects.requireNonNull(commandManager, "commandManager");
        Objects.requireNonNull(bubbleSpawnManager, "bubbleSpawnManager");

        BubbleProfile TEST_BUBBLE_PROFILE = new BubbleProfileImpl(
                UUID.randomUUID(),
                new DefaultBubbleFactory(componentRegistry, new ArmorStandBubbleFactory(0.25)),
                Set.of(
                        new BubbleProfile.KeyToFactoryEntry<PositionComponent>(
                                PositionComponent.key(), PositionComponent.factory(new OffsetComponentScoped(0, 2.5, 0))
                        ),
                        new BubbleProfile.KeyToFactoryEntry<>(
                                ViewComponent.key(),
                                ViewComponent.factory(
                                        MessageOverHeadPlugin.DEFAULT_ONLINE_PLAYER_PROVIDER,
                                        new ViewComponent.Settings(20, 5)
                                )
                        )
                )
        );

        commandManager.command(
                commandManager.commandBuilder("bubble-test")
                        .senderType(Player.class)
                        .required(TEXT, StringParser.greedyStringParser())
                        .handler(context -> {
                            Player player = context.sender();
                            String text = context.optional(TEXT).orElseThrow();
                            PlayerAuthor author = new PlayerAuthor(player);

                            bubbleSpawnManager.spawnBubble(
                                    new Message(Component.text(text)),
                                    author,
                                    author.getPosition(),
                                    TEST_BUBBLE_PROFILE
                            );
                        })
        );
    }
}
