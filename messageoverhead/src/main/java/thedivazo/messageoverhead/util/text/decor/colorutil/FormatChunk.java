package thedivazo.messageoverhead.util.text.decor.colorutil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import thedivazo.messageoverhead.util.text.decor.TextFormat;

import java.util.HashSet;
import java.util.Set;

@Getter
public class FormatChunk {
    private final String text;
    private final Set<TextFormat> textFormats;

    @Builder
    public FormatChunk(String text, @Singular Set<TextFormat> textFormats) {
        this.text = text;
        this.textFormats = new HashSet<>(textFormats);
    }
}
