package thedivazo.utils;

import net.md_5.bungee.api.ChatColor;
import java.awt.*;
import thedivazo.MessageOverHear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringColor {
    public static final Pattern HEX_PAT = Pattern.compile("&#[a-fA-F0-9]{6}");
    public static final Pattern HEX_PATv2 = Pattern.compile("\\{#([a-fA-F0-9]{6})}");
    private static final Pattern HEX_GRADIENT = Pattern.compile("\\{#([a-fA-F0-9]{6})(:#([a-fA-F0-9]{6}))+( )([^{}])*(})");

    public static final Pattern COLOR_SPIGOT_PAT = Pattern.compile("&[0-9a-flnrkmo]");

    public static final Pattern CHAT_COLOR_PAT = Pattern.compile(ChatColor.COLOR_CHAR +"[0-9a-fxlnrkmo]");

    public static int lengthColor(String string) {
        StringBuilder colorLine = new StringBuilder();
        Matcher colorMatcher = CHAT_COLOR_PAT.matcher(string);
        while (colorMatcher.find()) {
            colorLine.append(colorMatcher.group());
        }
        return colorLine.length();
    }

    public static String toNoColorString(String string) {

        return ofText(string).replaceAll(CHAT_COLOR_PAT.pattern(), "");
    }

    private static Color calculateGradientColor(int x, int parts, Color from, Color to) {
        double p = (double) (parts - x + 1) / (double) parts;

        return new Color(
                (int) (from.getRed() * p + to.getRed() * (1 - p)),
                (int) (from.getGreen() * p + to.getGreen() * (1 - p)),
                (int) (from.getBlue() * p + to.getBlue() * (1 - p))
        );
    }

    //cutting for other plugin (Chatty)
    private static String ofGadientColor(String text) {

        Matcher matcher = HEX_GRADIENT.matcher(text);

        StringBuffer stringBuffer = new StringBuffer();

        while (matcher.find()) {
            String gradient = matcher.group();
            int groups = 0;
            for (int i = 1; gradient.charAt(i) == '#'; i += 8) {
                groups++;
            }

            Color[] colors = new Color[groups];
            for (int i = 0; i < groups; i++) {
                colors[i] = ChatColor.of(gradient.substring((8 * i) + 1, (8 * i) + 8)).getColor();
            }

            String substring = gradient.substring((groups - 1) * 8 + 9, gradient.length() - 1);

            char[] chars = substring.toCharArray();

            StringBuilder gradientBuilder = new StringBuilder();

            int colorLength = chars.length / (colors.length - 1);
            int lastColorLength;
            if (colorLength == 0) {
                colorLength = 1;
                lastColorLength = 1;
                colors = Arrays.copyOfRange(colors, 0, chars.length);
            } else {
                lastColorLength = chars.length % (colorLength * (colors.length - 1)) + colorLength;
            }

            List<ChatColor> currentStyles = new ArrayList<>();
            for (int i = 0; i < (colors.length - 1); i++) {
                int currentColorLength = ((i == colors.length - 2) ? lastColorLength : colorLength);
                for (int j = 0; j < currentColorLength; j++) {
                    Color color = calculateGradientColor(j + 1, currentColorLength, colors[i], colors[i + 1]);
                    ChatColor chatColor = ChatColor.of(color);

                    int charIndex = colorLength * i + j;
                    if (charIndex + 1 < chars.length) {
                        if (chars[charIndex] == '&' || chars[charIndex] == '\u00A7') {
                            if (chars[charIndex + 1] == 'r') {
                                currentStyles.clear();
                                j++;
                                continue;
                            }

                            ChatColor style = ChatColor.getByChar(chars[charIndex + 1]);
                            if (style != null) {
                                currentStyles.add(style);
                                j++;
                                continue;
                            }
                        }
                    }

                    StringBuilder builder = gradientBuilder.append(chatColor.toString());

                    for (ChatColor currentStyle : currentStyles) {
                        builder.append(currentStyle.toString());
                    }

                    builder.append(chars[charIndex]);
                }
            }

            matcher.appendReplacement(stringBuffer, gradientBuilder.toString());
        }

        matcher.appendTail(stringBuffer);
        text = stringBuffer.toString();

        matcher = HEX_PATv2.matcher(text);
        stringBuffer = new StringBuffer();

        while (matcher.find()) {
            String hexColorString = matcher.group();
            matcher.appendReplacement(stringBuffer, ChatColor.of(hexColorString.substring(1, hexColorString.length() - 1)).toString());
        }

        matcher.appendTail(stringBuffer);


        return ChatColor.translateAlternateColorCodes('&', stringBuffer.toString());
    }

    public static String ofText(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (MessageOverHear.getVersion() < 1.16f) {
            return message;
        }
        Matcher match = HEX_PAT.matcher(message);
        while(match.find()) {
            String color = message.substring(match.start(), match.end());
            message = message.replace(color, ChatColor.of(color.replace("&", "")) + "");
        }
        message = ofGadientColor(message);
        return message;
    }

    public static List<String> ofText(List<String> messages) {
        StringBuilder shiftColor = new StringBuilder();
        ArrayList<String> colorLines = new ArrayList<>();
        for (String message : messages) {
            colorLines.add(shiftColor + message);
            StringUtil.stripsSymbol(StringColor.ofText(message), StringColor.CHAT_COLOR_PAT)
                    .forEach((index, color)->shiftColor.append(color));
        }
        return colorLines;
    }
}
