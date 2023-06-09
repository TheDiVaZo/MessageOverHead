package thedivazo.utils.text;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ColoredString implements CharSequence {

    private static final Pattern COLOR_HEX_GRADIENT = Pattern.compile("\\{#([a-fA-F0-9]{6})(:#([a-fA-F0-9]{6}))+( )([^{}])*(})"); //{#123456:#123456:#123456 exampleText}

    private static final Pattern COLOR_HEXv1 = Pattern.compile("#[a-fA-F0-9]{6}");

    private static final Pattern COLOR_DEFAULT = Pattern.compile("&[a-fkmolnr]");

    private static final Pattern unifyingPattern = Pattern.compile(String.format("(%s)|(%s)|(%s)",COLOR_HEX_GRADIENT.pattern(), COLOR_DEFAULT.pattern(), COLOR_HEXv1.pattern()));
    private Chunk[] chunks;

    public ColoredString valueOf(String str) {
        Matcher matcher = unifyingPattern.matcher(str);
        List<Chunk> newChunks = new ArrayList<>();
        int prevStart = 0;
        String prevColor = "&f";
        while (matcher.find()) {
            MatchResult matchResult = matcher.toMatchResult();
            int startColorIndex = matchResult.start();
            int endColorIndex = matchResult.end();
            String color = matcher.group();
            if(startColorIndex != 0 || !newChunks.isEmpty()) {
                newChunks.add(new Chunk(str.substring(prevStart, startColorIndex), getColor(prevColor)));
            //Градиент эта хуета не учитывает блиать
            }
            prevStart = endColorIndex;
            prevColor = color;
        }

        return new ColoredString(newChunks.toArray(Chunk[]::new));
    }

    public Color[] getColor(String string) {
        return null;
    }



    private Chunk[] gradientStringToChunk(String coloredString) {
        String preparedColoredString = coloredString.substring(1,coloredString.length()-1);
        Color[] colorList = Arrays.stream(
                        preparedColoredString
                                .substring(0, preparedColoredString.indexOf(' '))
                                .split(":"))
                .map(color -> ChatColor.of(color).getColor()).toArray(Color[]::new);
        String text = preparedColoredString.substring(preparedColoredString.indexOf(' ')+1);
        Color[] colors = getGradient(colorList, text.length());
        Iterator<Color> colorIterator = Arrays.stream(colors).iterator();
        return Arrays.stream(text
                .split(""))
                .map(letter->new Chunk(letter, colorIterator.next()))
                .collect(Collectors.toList())
                .toArray(Chunk[]::new);
    }

    private Color calculateColorPositionInGradient(Color one, Color two, int numberOfColors, int colorPosition) {
        double coefficientRedChannel = (double) Math.abs(one.getRed() - two.getRed()) / numberOfColors-1;
        double coefficientGreenChannel = (double) Math.abs(one.getGreen() - two.getGreen()) / numberOfColors-1;
        double coefficientBlueChannel = (double) Math.abs(one.getBlue() - two.getBlue()) / numberOfColors-1;

        return new Color(
                Math.round(Math.max(one.getRed(), two.getRed()) - coefficientRedChannel*colorPosition),
                Math.round(Math.max(one.getGreen(), two.getGreen()) - coefficientGreenChannel*colorPosition),
                Math.round(Math.max(one.getBlue(), two.getBlue()) - coefficientBlueChannel*colorPosition)

        );
    }

    private Color[] getGradient(Color[] colors, int length) {
        if (colors.length >= length) return colors;
        Color[] resultColors = new Color[length];

        int firstChunk = (int) Math.ceil((double) (colors.length-1)/length);
        int otherChunk = (colors.length-1)/length;
        for (int i = 0; i < length; i+= 1) {
            int colorIndex =(int) ((double) (colors.length-1)/length * i);
            Color one = colors[colorIndex];
            Color two = colors[colorIndex+1];
            int numberOfColors = i == 0 ? firstChunk : otherChunk;
            int position = i - colorIndex == 1 ? firstChunk : firstChunk + (colorIndex-1)*otherChunk;
            resultColors[i] = calculateColorPositionInGradient(one, two, numberOfColors, position);
        }

        return resultColors;
    }

    @Override
    public int length() {
        return Arrays.stream(chunks).map(Chunk::length).reduce(Integer::sum).orElse(0);
    }

    @Override
    public char charAt(int index) {
        return 0;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        List<Chunk> trimmedList = new ArrayList<>();
        int remainingCount = end - start;
        for (Chunk chunk : chunks) {
            int sublistSize = chunk.length();

            if (start < sublistSize) {
                end = Math.min(start + remainingCount, sublistSize);
                Chunk trimmedSublist = chunk.substring(start, end);
                trimmedList.add(trimmedSublist);
                remainingCount -= (end - start);
                if (remainingCount <= 0) {
                    break;
                }
            }

            start = Math.max(start - sublistSize, 0);
        }

        return new ColoredString(trimmedList.toArray(Chunk[]::new));
    }

    @Override
    public String toString() {
        return Arrays.stream(chunks).map(Chunk::toString).reduce((result, chunk)->result+chunk).orElse("");
    }
}
