package me.thedivazo.messageoverhead.core.text;

import me.thedivazo.messageoverhead.util.MinecraftVersion;
import org.intellij.lang.annotations.RegExp;

public interface LegacyTextWrapper extends TextWrapper {
    LegacyTextWrapper EMPTY = new LegacyTextWrapper() {
        @Override
        public String buildLegacySectionChar(MinecraftVersion version) {
            return "";
        }

        @Override
        public String plainText() {
            return "";
        }

        @Override
        public LegacyTextWrapper[] split(String separator) {
            return new LegacyTextWrapper[0];
        }
    };

    LegacyTextWrapper[] split(@RegExp String separator);

    // Returns the legacy Minecraft text format.
    String buildLegacySectionChar(MinecraftVersion version);
}
