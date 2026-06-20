package me.thedivazo.messageoverhead.armorstand;

import me.thedivazo.messageoverhead.MessageOverHeadPlugin;
import me.thedivazo.messageoverhead.core.Message;
import me.thedivazo.messageoverhead.core.render.RendererFactory;
import me.thedivazo.messageoverhead.core.render.RendererBubble;
import me.thedivazo.messageoverhead.util.MinecraftVersion;
import me.thedivazo.messageoverhead.util.Positionc;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Objects;

public class ArmoStandBubbleFactory implements RendererFactory {
    private static final LegacyComponentSerializer LEGACY_SERIALIZER =
            LegacyComponentSerializer.legacySection();
    private static final LegacyComponentSerializer RGB_LEGACY_SERIALIZER =
            LegacyComponentSerializer.builder()
                    .character(LegacyComponentSerializer.SECTION_CHAR)
                    .hexColors()
                    .useUnusualXRepeatedCharacterHexFormat()
                    .build();

    @Override
    public RendererBubble create(Message message, Positionc positionc) {
        return new BubbleArmorStand(serializeMessage(message.component()), positionc);
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
