package thedivazo.messageoverhead.util.text.decor.colorutil;

import lombok.Builder;
import lombok.Singular;
import thedivazo.messageoverhead.util.text.DecoratedChar;
import thedivazo.messageoverhead.util.text.decor.TextColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GradientChunk {
    TextColor startColor;
    TextColor endColor;
    List<FormatChunk> texts;
    private final int length;

    @Builder
    public GradientChunk(TextColor startColor, TextColor endColor, @Singular List<FormatChunk> texts) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.texts = texts;
        this.length = texts.stream().mapToInt(t -> t.getText().length()).reduce(Integer::sum).orElse(0);
    }

    public List<DecoratedChar> toDecoratedCharList() {
        List<DecoratedChar> result = new ArrayList<>();

        int prevPosition = 0;
        for (FormatChunk formatChunk : texts) {
            String text = formatChunk.getText();
            char[] chars = text.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                TextColor calculatedColor = calculateColor(i + prevPosition);
                result.add(DecoratedChar.builder()
                        .charWrapped(chars[i])
                        .color(calculatedColor)
                        .textFormats(formatChunk.getTextFormats())
                        .build());
            }
            prevPosition += chars.length;
        }
        return result;
    }

    private TextColor calculateColor(int position) {

        double startColorRed = startColor.getChatColor().getColor().getRed();
        double startColorGreen = startColor.getChatColor().getColor().getGreen();
        double startColorBlue = startColor.getChatColor().getColor().getBlue();

        double endColorRed = endColor.getChatColor().getColor().getRed();
        double endColorGreen = endColor.getChatColor().getColor().getGreen();
        double endColorBlue = endColor.getChatColor().getColor().getBlue();

        double differenceRed = (endColorRed - startColorRed) / length - 1;
        double differenceGreen = (endColorGreen - startColorGreen) / length - 1;
        double differenceBlue = (endColorBlue - startColorBlue) / length - 1;

        Color calculatedColor = new Color(
                Math.round(startColorRed + differenceRed * position),
                Math.round(startColorGreen + differenceGreen * position),
                Math.round(startColorBlue + differenceBlue * position)
        );
        return TextColor.of(calculatedColor);
    }
}
