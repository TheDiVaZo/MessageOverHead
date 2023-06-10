package thedivazo.utils.text;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ColoredString implements CharSequence {

    private static final Pattern HEX_GRADIENT = Pattern.compile("\\{#([a-fA-F0-9]{6})(:#([a-fA-F0-9]{6}))+( )([^{}])*(})"); //{#123456:#123456:#123456 exampleText}

    private static final Pattern COLOR_HEX = Pattern.compile("(&#[a-fA-F0-9]{6})+");

    private static final Pattern COLOR_DEFAULT = Pattern.compile("(&[a-fkmolnr])+");

    private Chunk[] chunks;

    enum TypeColor {
        GRADIENT,
        COLOR
    }

    @AllArgsConstructor
    @Getter
    static class ColorMatchResultWrapper {
        private MatchResult matchResult;
        private TypeColor typeColor;

        public int start() {
            return matchResult.start();
        }

        public int start(int group) {
            return matchResult.start(group);
        }

        public int end() {
            return matchResult.end();
        }

        public int end(int group) {
            return matchResult.end(group);
        }

        public String group() {
            return matchResult.group();
        }

        public String group(int group) {
            return matchResult.group(group);
        }

        public int groupCount() {
            return matchResult.groupCount();
        }
    }

    public static ColoredString valueOf(String str) {
        List<Chunk> result = new ArrayList<>();

        Matcher gradientMatcher = HEX_GRADIENT.matcher(str);
        Matcher colorHexMatcher = COLOR_HEX.matcher(str);
        Matcher colorDefaultMatcher = COLOR_DEFAULT.matcher(str);

        List<ColorMatchResultWrapper> matchResults = new ArrayList<>();

        while (true) {
            if (gradientMatcher.find()) {
                matchResults.add(new ColorMatchResultWrapper(gradientMatcher.toMatchResult(), TypeColor.GRADIENT));
            }
            else if (colorHexMatcher.find()) {
                matchResults.add(new ColorMatchResultWrapper( colorHexMatcher.toMatchResult(), TypeColor.COLOR));
            }
            else if (colorDefaultMatcher.find()) {
                matchResults.add(new ColorMatchResultWrapper(colorDefaultMatcher.toMatchResult(), TypeColor.COLOR));
            }
            else break;
        }

        matchResults.sort(Comparator.comparingInt(ColorMatchResultWrapper::start));

        ListIterator<ColorMatchResultWrapper> matchResultIterator = matchResults.listIterator();
        int prevStart = 0;
        ChatColor[] prevColor = new ChatColor[0];
        while (matchResultIterator.hasNext()) {
            ColorMatchResultWrapper matchResult = matchResultIterator.next();
            if (matchResult.getTypeColor() == TypeColor.COLOR) {
                if (matchResult.start() != prevStart) {
                    result.add(new Chunk(str.substring(prevStart, matchResult.start()), prevColor));
                }
                prevStart = matchResult.end();
                prevColor = getColor(matchResult.group());
            }
            else {
                if (matchResult.start() != prevStart) {
                    result.add(new Chunk(str.substring(prevStart, matchResult.start()), prevColor));
                }
                result.addAll(List.of(getGradient(matchResult.group())));
                prevStart = matchResult.end();
                prevColor = new ChatColor[0];
            }
        }
        if (prevStart != str.length()-1) result.add(new Chunk(str.substring(prevStart), prevColor));
        return new ColoredString(result.toArray(Chunk[]::new));
    }

    private static ChatColor[] getColor(String str) {
        if (str.isEmpty() || str.isBlank()) return new ChatColor[0];
        else return Arrays.stream(str.split("&"))
                .filter(color->!color.isBlank() && !color.isEmpty())
                .map(color->
                        color.length() == 1 ? ChatColor.getByChar(color.charAt(0)) : ChatColor.of(color))
                .collect(Collectors.toList())
                .toArray(ChatColor[]::new);
    }

    private static Chunk[] getGradient(String coloredString) {
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

    private static Color calculateColorPositionInGradient(Color one, Color two, int numberOfColors, int colorPosition) {
        double coefficientRedChannel = (double) (one.getRed() - two.getRed()) / (numberOfColors-1);
        double coefficientGreenChannel = (double) (one.getGreen() - two.getGreen()) / (numberOfColors-1);
        double coefficientBlueChannel = (double) (one.getBlue() - two.getBlue()) / (numberOfColors-1);

        return new Color(
                (int) Math.round(one.getRed() - coefficientRedChannel*colorPosition),
                (int) Math.round(one.getGreen() - coefficientGreenChannel*colorPosition),
                (int) Math.round(one.getBlue()- coefficientBlueChannel*colorPosition)

        );
    }

    private static Color[] getGradient(Color[] colors, int length) {
        if (colors.length >= length) return colors;
        Color[] resultColors = new Color[length];

        int firstChunk = (int) Math.ceil((double) length/(colors.length-1));
        int otherChunk = length/(colors.length-1);
        for (int i = 0; i < length; i+= 1) {
            int colorIndex =(int) ((double) (colors.length-1)/length * i);
            Color one = colors[colorIndex];
            Color two = colors[colorIndex+1];
            int numberOfColors = i == 0 ? firstChunk : otherChunk;
            int position = colorIndex == 0 ? i : i - colorIndex * otherChunk;
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
