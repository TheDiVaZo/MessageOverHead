package me.thedivazo.messageoverhead.core.text;

import me.thedivazo.messageoverhead.util.AdventureUtil;
import me.thedivazo.messageoverhead.util.MinecraftVersion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.intellij.lang.annotations.RegExp;

public class PaperComponentWrapper implements LegacyTextWrapper {
    private static final LegacyComponentSerializer LEGACY_SERIALIZER =
            LegacyComponentSerializer.legacySection();
    private static final LegacyComponentSerializer RGB_LEGACY_SERIALIZER =
            LegacyComponentSerializer.builder()
                    .character(LegacyComponentSerializer.SECTION_CHAR)
                    .hexColors()
                    .useUnusualXRepeatedCharacterHexFormat()
                    .build();

    private final Component components;
    private final String plainText;

    public PaperComponentWrapper(Component components) {
        this.components = components;
        this.plainText = PlainTextComponentSerializer.plainText().serialize(components);
    }

    @Override
    public String plainText() {
        return plainText;
    }

    @Override
    public PaperComponentWrapper[] split(@RegExp String separator) {
        return AdventureUtil.split(components, separator).stream()
                .map(PaperComponentWrapper::new)
                .toArray(PaperComponentWrapper[]::new);
    }

    @Override
    public String buildLegacySectionChar(MinecraftVersion serverVersion) {
        LegacyComponentSerializer serializer =
                serverVersion.isAtLeast(MinecraftVersion.VERSION_1_16)
                        ? RGB_LEGACY_SERIALIZER
                        : LEGACY_SERIALIZER;

        return serializer.serialize(components);
    }
}
