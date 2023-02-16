package thedivazo.utils;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import thedivazo.MessageOverHead;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    private StringUtil() {
    }

    public static LinkedHashMap<Integer, String> stripsSymbol(String str, Pattern pattern) {
        Matcher colorMatcher = pattern.matcher(str);
        LinkedHashMap<Integer, String> colorCodesAndInexs = new LinkedHashMap<>();
        int lengthColorSum = 0;
        while (colorMatcher.find()) {
            String color = colorMatcher.group();
            int colorIndex = str.indexOf(color, lengthColorSum);
            lengthColorSum = color.length() + colorIndex;
            colorCodesAndInexs.put(colorIndex, color);
        }
        return colorCodesAndInexs;
    }

    public static List<LinkedHashMap<Integer, String>> stripsSymbol(List<String> strArray, Pattern pattern) {
        List<LinkedHashMap<Integer, String>> colorCodesAndInexsArray = new ArrayList<>();
        for (int i = 0; i < strArray.size(); i++) {
            String str = strArray.get(i);
            colorCodesAndInexsArray.add(stripsSymbol(str, pattern));
        }
        return colorCodesAndInexsArray;
    }

    public static String insertsSymbol(LinkedHashMap<Integer, String> colorCodesAndInexs, String str) {
        Iterator<Integer> iterator = colorCodesAndInexs.keySet().iterator();
        StringBuilder result = new StringBuilder();
        int shiftIndex = 0;
        int lengthColorSum = 0;
        while (iterator.hasNext()) {
            int colorIndex = iterator.next();
            if(colorIndex-lengthColorSum>str.length()) break;
            String colorCode = colorCodesAndInexs.get(colorIndex);
            String intermediateResult = str.substring(shiftIndex, colorIndex-lengthColorSum) + colorCode;
            shiftIndex = colorIndex - lengthColorSum;
            lengthColorSum+= colorCode.length();
            result.append(intermediateResult);
        }
        String intermediateResult = str.substring(shiftIndex);
        result.append(intermediateResult);
        return result.toString();
    }

    public static LinkedHashMap<Integer, String> stripsSymbol(Pattern pattern, List<String> strArray) {
        LinkedHashMap<Integer, String> colorCodesAndInexs = new LinkedHashMap<>();
        int shiftIndex = 0;
        for (int i = 0; i < strArray.size(); i++) {
            String str = strArray.get(i);

            for (Map.Entry<Integer, String> indexAndCode : stripsSymbol(str, pattern).entrySet()) {
                int index = indexAndCode.getKey();
                String code = indexAndCode.getValue();
                colorCodesAndInexs.put(index+shiftIndex, code);
            }
            shiftIndex+=str.length();
        }
        return colorCodesAndInexs;
    }

    public static List<String> insertsSymbol(List<LinkedHashMap<Integer, String>> colorCodesAndInexs, List<String> strArray) {
        List<String> result = new ArrayList<>(strArray.size());
        for (int i = 0; i < strArray.size(); i++) {
            result.add(insertsSymbol(colorCodesAndInexs.get(i), strArray.get(i)));
        }
        return result;
    }

    public static List<String> insertsSymbol(LinkedHashMap<Integer, String> colorCodesAndInexs, List<String> strArray) {
        List<String> result = new ArrayList<>(strArray.size());
        int shiftIndex = 0;
        for (int i = 0; i < strArray.size(); i++) {
            String str = strArray.get(i);
            LinkedHashMap<Integer, String> colorCodesAndIndexsForStr = new LinkedHashMap<>();
            for (Map.Entry<Integer, String> indexAndCode : colorCodesAndInexs.entrySet()) {
                int index = indexAndCode.getKey();
                String code = indexAndCode.getValue();
                if(shiftIndex >= index && index >= str.length()+shiftIndex) {
                    break;
                }
                int realIndex = index - shiftIndex;
                if(realIndex >= 0) {
                    colorCodesAndIndexsForStr.put(realIndex, code);
                }
            }
            String intermediateResult = insertsSymbol(colorCodesAndIndexsForStr, str);
            result.add(intermediateResult);
            shiftIndex += intermediateResult.length();
        }
        return result;
    }


    public static List<String> splitText(String text, int maxSizeLine) {
        ArrayList<String> result = new ArrayList<>();

        Pattern pattern = Pattern.compile("(\\S{0,"+maxSizeLine+"}\\s?)");
        Matcher matcher = pattern.matcher(text);
        StringBuilder testString = new StringBuilder();
        while (matcher.find()) {
            if (testString.length() < maxSizeLine) {
                testString.append(matcher.group());
            }
            else {
                result.add(testString.toString());
                testString = new StringBuilder(matcher.group());
            }
        }
        result.add(testString.toString());

        return result;
    }

    public static String setEmoji(Player player, String text) {
        if(text==null) return text;
        if(MessageOverHead.getConfigManager().isIALoaded()) text = FontImageWrapper.replaceFontImages(player, text);
        return text;
    }

    public static String setPlaceholders(Player player, String text) {
        if(MessageOverHead.getConfigManager().isPAPILoaded() && text!=null) text = PlaceholderAPI.setPlaceholders(player, text);
        return text;
    }
}
