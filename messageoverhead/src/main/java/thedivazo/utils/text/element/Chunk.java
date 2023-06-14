package thedivazo.utils.text.element;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import thedivazo.utils.text.customize.TextColor;
import thedivazo.utils.text.customize.TextFormatting;

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

    public int indexOf(int ch) {
        return text.indexOf(ch);
    }

    public int indexOf(int ch, int fromIndex) {
        return text.indexOf(ch, fromIndex);
    }

    public int lastIndexOf(int ch) {
        return text.lastIndexOf(ch);
    }

    public int lastIndexOf(int ch, int fromIndex) {
        return text.lastIndexOf(ch, fromIndex);
    }

    public int indexOf(@NotNull String str) {
        return text.indexOf(str);
    }

    public int indexOf(@NotNull String str, int fromIndex) {
        return text.indexOf(str, fromIndex);
    }

    public int lastIndexOf(@NotNull String str) {
        return text.lastIndexOf(str);
    }

    public int lastIndexOf(@NotNull String str, int fromIndex) {
        return text.lastIndexOf(str, fromIndex);
    }
}
