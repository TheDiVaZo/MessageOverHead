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

    public boolean equalsDecorate(Chunk chunk) {
        return textFormatting.equals(chunk.textFormatting) && color.equals(chunk.color);
    }

    public boolean equalsDecorate(DecoratedChar chunk) {
        return textFormatting.equals(chunk.getTextFormatting()) && color.equals(chunk.getColor());
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

    public int indexOf(DecoratedChar decoratedChar) {
        return indexOf(decoratedChar, 0);
    }

    public int indexOf(DecoratedChar decoratedChar, int fromIndex) {
        return equalsDecorate(decoratedChar) ? indexOf(decoratedChar.getCharWrapped(), fromIndex) : -1;
    }

    public int lastIndexOf(DecoratedChar decoratedChar) {
        return lastIndexOf(decoratedChar, text.length());
    }

    public int lastIndexOf(DecoratedChar decoratedChar, int fromIndex) {
        return equalsDecorate(decoratedChar) ? lastIndexOf(decoratedChar.getCharWrapped(), fromIndex) : -1;
    }

    public boolean isEmpty() {
        return text.isEmpty();
    }

    public int indexOf(Chunk chunk) {
        return equalsDecorate(chunk) ? indexOf(chunk.text) : -1;
    }

    public boolean endsWith(Chunk chunk) {
        return equalsDecorate(chunk) && text.endsWith(chunk.text);
    }

    public boolean startsWith(Chunk chunk) {
        return equalsDecorate(chunk) && text.startsWith(chunk.text);
    }
}
