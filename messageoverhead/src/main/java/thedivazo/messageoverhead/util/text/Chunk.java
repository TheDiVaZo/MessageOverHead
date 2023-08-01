package thedivazo.messageoverhead.util.text;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import thedivazo.messageoverhead.util.text.decor.TextColor;
import thedivazo.messageoverhead.util.text.decor.TextFormat;

import java.util.Set;

@ToString
@Getter
@EqualsAndHashCode
@Builder(setterPrefix = "set", toBuilder = true)
public class Chunk implements CharSequence {

    @Builder.Default
    private final String text = "";

    @Builder.Default
    private final TextColor color = TextColor.WHITE;

    @Singular
    private final Set<TextFormat> textFormats;


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
        return new Chunk(text.substring(start, end), color, textFormats);
    }

    public boolean equalsDecorate(Chunk chunk) {
        return textFormats.equals(chunk.textFormats) && color.equals(chunk.color);
    }

    public boolean equalsDecorate(DecoratedChar chunk) {
        return textFormats.equals(chunk.getTextFormat()) && color.equals(chunk.getColor());
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

    public int indexOf(Chunk chunk, int fromIndex) {
        return equalsDecorate(chunk) ? indexOf(chunk.text, fromIndex) : -1;
    }

    public int lastIndexOf(Chunk chunk, int fromIndex) {
        return equalsDecorate(chunk) ? lastIndexOf(chunk.text, fromIndex) : -1;
    }

    public int lastIndexOf(Chunk chunk) {
        return equalsDecorate(chunk) ? lastIndexOf(chunk.text) : -1;
    }

    public boolean endsWith(DecoratedChar decoratedChar) {
        return equalsDecorate(decoratedChar) && text.endsWith(String.valueOf(decoratedChar.getCharWrapped()));
    }

    public boolean endsWith(String str) {
        return text.endsWith(str);
    }

    public boolean endsWith(Chunk chunk) {
        return equalsDecorate(chunk) && text.endsWith(chunk.text);
    }

    public boolean startsWith(DecoratedChar decoratedChar) {
        return equalsDecorate(decoratedChar) && text.startsWith(String.valueOf(decoratedChar.getCharWrapped()));
    }

    public boolean startsWith(String str) {
        return text.startsWith(str);
    }

    public boolean startsWith(Chunk chunk) {
        return equalsDecorate(chunk) && text.startsWith(chunk.text);
    }

    public boolean contains(String str) {
        return text.contains(str);
    }

    public boolean contains(DecoratedChar decoratedChar) {
        return equalsDecorate(decoratedChar) && text.contains(String.valueOf(decoratedChar.getCharWrapped()));
    }

    public boolean contains(Chunk chunk) {
        return equalsDecorate(chunk) && text.contains(chunk.text);
    }

    public Chunk replace(char oldChar, char newChar) {
        return toBuilder().setText(getText().replace(oldChar, newChar)).build();
    }

    public Chunk replace(String oldStr, String newStr) {
        return toBuilder().setText(getText().replace(oldStr, newStr)).build();
    }

    public Chunk trim() {
        return toBuilder().setText(getText().trim()).build();
    }

    public Chunk trimStart() {
        return toBuilder().setText(getText().replaceAll("^ +", "")).build();
    }

    public Chunk trimEnd() {
        return toBuilder().setText(getText().replaceAll(" +$","")).build();
    }
}
