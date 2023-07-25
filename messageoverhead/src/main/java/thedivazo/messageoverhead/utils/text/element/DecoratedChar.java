package thedivazo.messageoverhead.utils.text.element;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import thedivazo.messageoverhead.utils.text.customize.TextColor;
import thedivazo.messageoverhead.utils.text.customize.TextFormatting;

import java.util.Set;

@Getter
@Builder
public class DecoratedChar {
    private char charWrapped;

    private final TextColor color;

    @Singular("format")
    private final Set<TextFormatting> textFormatting;
}
