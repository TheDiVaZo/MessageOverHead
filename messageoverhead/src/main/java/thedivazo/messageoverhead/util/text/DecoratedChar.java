package thedivazo.messageoverhead.util.text;

import lombok.*;
import thedivazo.messageoverhead.util.text.decor.TextColor;
import thedivazo.messageoverhead.util.text.decor.TextFormat;

import java.util.Set;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class DecoratedChar {
    private char charWrapped;

    private final TextColor color;

    private final Set<TextFormat> textFormat;
}
