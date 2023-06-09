package thedivazo.utils.text;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import io.th0rgal.oraxen.shaded.jetbrains.annotations.NotNull;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextColorProcessor {
    private TextColorProcessor(){}

    private static final Pattern COLOR_HEX_GRADIENT = Pattern.compile("\\{#([a-fA-F0-9]{6})(:#([a-fA-F0-9]{6}))+( )([^{}])*(})"); //{#123456:#123456:#123456 exampleText}

    private static final Pattern COLOR_HEXv1 = Pattern.compile("#[a-fA-F0-9]{6}");

    public static String parseAllColorPatternsInString(String text) {
        return parseDefaultColor(parseHexToColor(parseGradientToColor(text)));
    }

    public static String parseAllColorPatternsInText(List<String> text, boolean wrapColor) {
        return null;
    }

    public static String parseGradientToColor(String text) {
        Matcher gradientMatcher = COLOR_HEX_GRADIENT.matcher(text);
        StringBuilder preparedText = new StringBuilder();
        while (gradientMatcher.find()) {
            String coloredString = gradientMatcher.group(0);
            gradientMatcher.appendReplacement(preparedText, convertGradientToColoredText(coloredString));
            gradientMatcher.appendTail(preparedText);
        }
        return preparedText.toString().isEmpty() ? text : preparedText.toString();
    }

    public static String parseHexToColor(String text) {
        Matcher hexMatcher = COLOR_HEXv1.matcher(text);
        StringBuilder preparedText = new StringBuilder();
        while (hexMatcher.find()) {
            String coloredString = hexMatcher.group(0);
            hexMatcher.appendReplacement(preparedText, ChatColor.of(coloredString).toString());
            hexMatcher.appendTail(preparedText);
        }
        return preparedText.toString().isEmpty() ? text : preparedText.toString();
    }

    public static String parseDefaultColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    private static String convertGradientToColoredText(String coloredString) {
        String preparedColoredString = coloredString.substring(1,coloredString.length()-1);
        Color[] colorList = Arrays.stream(
                preparedColoredString
                        .substring(0, preparedColoredString.indexOf(' '))
                        .split(":"))
                .map(color -> ChatColor.of(color).getColor()).toArray(Color[]::new);
        String text = preparedColoredString.substring(preparedColoredString.indexOf(' ')+1);
        Color[] colors = getGradient(colorList, text.length());
        Iterator<Color> colorIterator = Arrays.stream(colors).iterator();
        return Arrays
                .stream(text.split(""))
                .map(
                        letter->ChatColor.of(colorIterator.next())+letter
                ).collect(Collectors.joining(""));
    }

    private static Color calculateColor(Color one, Color two, int numberOfColors, int colorPosition) {
        double coefficientRedChannel = (double) Math.abs(one.getRed() - two.getRed()) / numberOfColors-1;
        double coefficientGreenChannel = (double) Math.abs(one.getGreen() - two.getGreen()) / numberOfColors-1;
        double coefficientBlueChannel = (double) Math.abs(one.getBlue() - two.getBlue()) / numberOfColors-1;

        return new Color(
                Math.round(Math.max(one.getRed(), two.getRed()) - coefficientRedChannel*colorPosition),
                Math.round(Math.max(one.getGreen(), two.getGreen()) - coefficientGreenChannel*colorPosition),
                Math.round(Math.max(one.getBlue(), two.getBlue()) - coefficientBlueChannel*colorPosition)

        );
    }

    private static Color[] getGradient(Color[] colors, int length) {
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
            resultColors[i] = calculateColor(one, two, numberOfColors, position);
        }

        return resultColors;
    }

    public static String setEmoji(Player player,@NotNull String text) {
        if(Bukkit.getPluginManager().isPluginEnabled("ItemsAdder")) text = FontImageWrapper.replaceFontImages(player, text);
        return text;
    }

    public static String setPlaceholders(Player player,@NotNull String text) {
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) text = PlaceholderAPI.setPlaceholders(player, text);
        return text;
    }
}
