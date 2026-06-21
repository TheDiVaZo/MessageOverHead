package me.thedivazo.messageoverhead.armorstand;

import kotlin.collections.CollectionsKt;
import me.thedivazo.messageoverhead.MessageOverHeadPlugin;
import me.thedivazo.messageoverhead.core.Message;
import me.thedivazo.messageoverhead.core.render.RendererBubble;
import me.thedivazo.messageoverhead.core.render.RendererFactory;
import me.thedivazo.messageoverhead.util.ComponentTextUtil;
import me.thedivazo.messageoverhead.util.MinecraftVersion;
import me.thedivazo.messageoverhead.util.Positionc;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;

import java.util.List;
import java.util.Objects;

public class ArmorStandBubbleFactory implements RendererFactory {
    private static final LegacyComponentSerializer LEGACY_SERIALIZER =
            LegacyComponentSerializer.legacySection();
    private static final LegacyComponentSerializer RGB_LEGACY_SERIALIZER =
            LegacyComponentSerializer.builder()
                    .character(LegacyComponentSerializer.SECTION_CHAR)
                    .hexColors()
                    .useUnusualXRepeatedCharacterHexFormat()
                    .build();

    private final double lineSpacing;

    public ArmorStandBubbleFactory(double lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    @Override
    public RendererBubble create(Message message, Positionc positionc) {
        Location loc = new Location(null, positionc.x(), positionc.y(), positionc.z());
        ArmorStand armorStand = new GroupedFakeArmorStand(
                CollectionsKt.map(
                        ComponentTextUtil.wrapMessage(message.component(), 28, 28),
                        component -> new FakeArmorStand(serializeMessage(component), loc)
                ),
                lineSpacing,
                positionc
        );
        return new BubbleArmorStand(armorStand, positionc);
    }

    private static String serializeMessage(Component message) {
        Objects.requireNonNull(message, "message");
        LegacyComponentSerializer serializer =
                MessageOverHeadPlugin.SERVER_VERSION.isAtLeast(MinecraftVersion.VERSION_1_16)
                        ? RGB_LEGACY_SERIALIZER
                        : LEGACY_SERIALIZER;
        return serializer.serialize(message);
    }
}
