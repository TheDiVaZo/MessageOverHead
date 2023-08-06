package thedivazo.messageoverhead.util.text.decor.colorutil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import thedivazo.messageoverhead.util.text.decor.TextFormat;

import java.util.Set;

@Getter
@AllArgsConstructor
@Builder
public class FormatChunk {
    private final String text;
    @Singular
    private final Set<TextFormat> textFormats;
}
