package thedivazo.utils;

import net.md_5.bungee.api.ChatColor;
import thedivazo.Main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorString {
    private static final Pattern HEX_PAT = Pattern.compile("&#[a-fA-F0-9]{6}");

    private static final Pattern COLOR_PAT = Pattern.compile("&[0-9a-flnrkmo]");

    private static final Pattern CHAT_COLOR_PAT = Pattern.compile(ChatColor.COLOR_CHAR +"[0-9a-fxlnrkmo]");

    public static int lengthString(String string) {
        return string.length() - lengthColor(string);
    }

    public static int lengthColor(String string) {
        StringBuilder colorLine = new StringBuilder();
        Matcher colorMatcher = CHAT_COLOR_PAT.matcher(string);
        while (colorMatcher.find()) {
            colorLine.append(colorMatcher.group());
        }
        return colorLine.length();
    }

    public static String substring (String string, int begindex, int endindex) { //обрезает строку, не учитывая символы цвета
        string += "\0";
        char[] chars = string.toCharArray();
        int letIndex = -1;
        Integer trueBeginIndex = null;
        Integer trueEndIndex = null;
        for(int i = 0; i < chars.length; i++) {
            char thisChar = chars[i];
            char nextChar;
            letIndex++;
            if(i+1 < chars.length) {
                nextChar = chars[i+1];
                String doubleChars = new String(new char[]{thisChar, nextChar});
                if(CHAT_COLOR_PAT.matcher(doubleChars).find()) {letIndex-=2;}
            }
            if(letIndex == begindex && trueBeginIndex == null) {
                trueBeginIndex = i;
            }
            if(letIndex == endindex && trueEndIndex == null) {
                trueEndIndex = i;
                break;
            }
        }
        return string.substring(trueBeginIndex, trueEndIndex);
    }


    public static String toNoColorString(String string) {
        string = string;
        return string.replaceAll(CHAT_COLOR_PAT.pattern(), "");
    }

    public static String ofLine(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (Main.getVersion() < 1.16f) {
            return message;
        }
        Matcher match = HEX_PAT.matcher(message);
        while(match.find()) {
            String color = message.substring(match.start(), match.end());
            message = message.replace(color, ChatColor.of(color.replace("&", "")) + "");
        }
        return message;
    }

    public static String[] ofText(String[] messages) {

        StringBuilder colorLineOld = new StringBuilder();
        for(int i = 0; i < messages.length; i++) {
            StringBuilder colorLine = new StringBuilder();
            Matcher colorMatcher = CHAT_COLOR_PAT.matcher(messages[i]);
            while (colorMatcher.find()) {
                colorLine.append(colorMatcher.group());
            }
            messages[i] = colorLineOld + messages[i];
            colorLineOld.append(colorLine);
        }
        return messages;
    }
}
