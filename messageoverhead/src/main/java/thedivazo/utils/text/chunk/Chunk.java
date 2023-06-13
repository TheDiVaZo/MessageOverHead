package thedivazo.utils.text.chunk;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import thedivazo.utils.text.customize.TextColor;
import thedivazo.utils.text.customize.TextFormatting;

import java.util.List;
import java.util.Set;

@ToString
@Getter
@EqualsAndHashCode
@Builder(setterPrefix = "set", toBuilder = true)
public class Chunk implements CharSequence {
    private final String text;

    private final TextColor color;

    @Singular("format")
    private final Set<TextFormatting> textFormatting;


    @Override
    public int length() {
        return text.length();
    }

    @Override
    public char charAt(int index) {
        return text.charAt(index);
    }

    @NotNull
    @Override
    public CharSequence subSequence(int start, int end) {
        return new Chunk(text.substring(start, end), color, textFormatting);
    }
}
