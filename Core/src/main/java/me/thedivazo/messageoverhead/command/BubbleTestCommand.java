package me.thedivazo.messageoverhead.command;

import me.thedivazo.messageoverhead.MessageOverHeadPlugin;
import me.thedivazo.messageoverhead.api.SpawnService;
import me.thedivazo.messageoverhead.armorstand.ArmorStandBubbleFactory;
import me.thedivazo.messageoverhead.core.DefaultBubbleFactory;
import me.thedivazo.messageoverhead.core.component.BubbleComponentFactory;
import me.thedivazo.messageoverhead.core.component.ComponentRegistry;
import me.thedivazo.messageoverhead.core.component.ComponentKey;
import me.thedivazo.messageoverhead.core.component.PositionComponent;
import me.thedivazo.messageoverhead.core.component.ViewComponent;
import me.thedivazo.messageoverhead.core.component.scope.ComponentScoped;
import me.thedivazo.messageoverhead.core.component.scope.OffsetComponentScoped;
import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.Message;
import me.thedivazo.messageoverhead.profile.ProfileId;
import me.thedivazo.messageoverhead.profile.BubbleProfile;
import me.thedivazo.messageoverhead.util.Position;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class BubbleTestCommand {
    private static final CloudKey<String> TEXT = CloudKey.of("text", String.class);

    private BubbleTestCommand() {
    }

    public static void register(
            LegacyPaperCommandManager<CommandSender> commandManager,
            SpawnService spawnService,
            ComponentRegistry componentRegistry
    ) {
        Objects.requireNonNull(commandManager, "commandManager");
        Objects.requireNonNull(spawnService, "spawnService");

        Map<ComponentKey<?>, BubbleComponentFactory<?>> componentFactories = new LinkedHashMap<>();
        componentFactories.put(
                PositionComponent.key(),
                PositionComponent.factory(List.<Function<ActiveBubble, ComponentScoped<Position>>>of(
                        activeBubble -> new OffsetComponentScoped(0, 2.5, 0)
                ))
        );
        componentFactories.put(
                ViewComponent.key(),
                ViewComponent.factory(
                        MessageOverHeadPlugin.DEFAULT_ONLINE_PLAYER_PROVIDER,
                        new ViewComponent.Settings(20, 5)
                )
        );

        BubbleProfile TEST_BUBBLE_PROFILE = BubbleProfile.create(
                ProfileId.of("test"),
                new DefaultBubbleFactory(componentRegistry, new ArmorStandBubbleFactory(0.25)),
                componentFactories
        );

        commandManager.command(
                commandManager.commandBuilder("bubble-test")
                        .senderType(Player.class)
                        .required(TEXT, StringParser.greedyStringParser())
                        .handler(context -> {
                            Player player = context.sender();
                            String text = context.optional(TEXT).orElseThrow();
                            PlayerAuthor author = new PlayerAuthor(player);

                            spawnService.spawnBubble(
                                    new Message(Component.text(text)),
                                    author,
                                    author.getPosition(),
                                    TEST_BUBBLE_PROFILE
                            );
                        })
        );
    }
}
