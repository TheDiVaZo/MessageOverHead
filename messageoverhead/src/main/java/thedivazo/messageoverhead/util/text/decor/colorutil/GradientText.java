package thedivazo.messageoverhead.util.text.decor.colorutil;

import lombok.Builder;
import lombok.Singular;
import thedivazo.messageoverhead.util.text.Chunk;
import thedivazo.messageoverhead.util.text.DecoratedChar;
import thedivazo.messageoverhead.util.text.DecoratedString;
import thedivazo.messageoverhead.util.text.decor.TextColor;
import thedivazo.messageoverhead.util.text.decor.TextFormat;
import thedivazo.messageoverhead.util.text.handler.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Builder
public class GradientText {
    private final static Pattern pattern = Pattern.compile("\\{GRADIENT:(.*)}");

    public static Pattern getPattern() {
        return pattern;
    }

    @Singular
    private final List<GradientChunk> gradientChunks;

    public DecoratedString toDecoratedString() {
        List<DecoratedChar> result = new ArrayList<>();
        for (GradientChunk gradientChunk : gradientChunks) {
            result.addAll(gradientChunk.toDecoratedCharList());
        }
        return DecoratedString.valueOf(result);
    }

    public List<Chunk> toChunkList() {
        List<Chunk> result = new ArrayList<>();
        for (GradientChunk gradientChunk : gradientChunks) {
            result.addAll(Chunk.from(gradientChunk.toDecoratedCharList()));
        }
        return result;
    }

    public static GradientText valueOf(String gradient) {
        Matcher matcher = pattern.matcher(gradient);
        if (!matcher.matches())
            throw new IllegalArgumentException("The string '" + gradient + "' does not match the pattern '" + pattern.pattern() + "'");

        String minecraftColoredString = matcher.group();

        List<BaseComponent> components = TextHandler.from(minecraftColoredString);

        List<GradientChunk> gradientChunks = new ArrayList<>();
        TextColor startColor = TextColor.WHITE;
        TextColor endColor = TextColor.WHITE;
        List<FormatChunk> textChunk = new ArrayList<>();

        Set<TextFormat> prevTextFormat = new HashSet<>();
        StringBuilder stringBuilder = new StringBuilder();

        for (BaseComponent component : components) {
            if (component instanceof ColorComponent) {
                TextColor color = ((ColorComponent) component).getColor();
                if (stringBuilder.length() == 0) startColor = color;
                else {
                    endColor = color;
                    textChunk.add(new FormatChunk(stringBuilder.toString(), prevTextFormat));
                    gradientChunks.add(new GradientChunk(startColor, endColor, textChunk));
                    startColor = color;
                    endColor = TextColor.WHITE;
                    stringBuilder.setLength(0);
                }
            } else if (component instanceof FormatComponent) {
                TextFormat format = ((FormatComponent) component).getFormat();
                if (stringBuilder.length() != 0) {
                    textChunk.add(new FormatChunk(stringBuilder.toString(), prevTextFormat));
                    stringBuilder.setLength(0);
                }
                if (format.equals(TextFormat.RESET)) prevTextFormat.clear();
                else {
                    prevTextFormat.add(format);
                }
            } else if (component instanceof TextComponent) {
                String text = ((TextComponent) component).getText();
                stringBuilder.append(text);
            }
        }
        if (stringBuilder.length() != 0) {
            textChunk.add(new FormatChunk(stringBuilder.toString(), prevTextFormat));
            stringBuilder.setLength(0);
            gradientChunks.add(new GradientChunk(startColor, endColor, textChunk));
        }

        return new GradientText(gradientChunks);
    }
}
