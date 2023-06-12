package thedivazo.utils.text.customize;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class TextColor implements TextChatColorDecorator {

    public static final TextColor BLACK = new TextColor(ChatColor.BLACK.getColor());
    public static final TextColor DARK_BLUE = new TextColor(ChatColor.DARK_BLUE.getColor());
    public static final TextColor DARK_GREEN = new TextColor(ChatColor.DARK_GREEN.getColor());
    public static final TextColor DARK_AQUA = new TextColor(ChatColor.DARK_AQUA.getColor());
    public static final TextColor DARK_RED = new TextColor(ChatColor.DARK_RED.getColor());
    public static final TextColor DARK_PURPLE = new TextColor(ChatColor.DARK_PURPLE.getColor());
    public static final TextColor GOLD = new TextColor(ChatColor.GOLD.getColor());
    public static final TextColor GRAY = new TextColor(ChatColor.GRAY.getColor());
    public static final TextColor DARK_GRAY = new TextColor(ChatColor.DARK_GRAY.getColor());
    public static final TextColor BLUE = new TextColor(ChatColor.BLUE.getColor());
    public static final TextColor GREEN = new TextColor(ChatColor.GREEN.getColor());
    public static final TextColor AQUA = new TextColor(ChatColor.AQUA.getColor());
    public static final TextColor RED = new TextColor(ChatColor.RED.getColor());
    public static final TextColor LIGHT_PURPLE = new TextColor(ChatColor.LIGHT_PURPLE.getColor());
    public static final TextColor YELLOW = new TextColor(ChatColor.YELLOW.getColor());
    public static final TextColor WHITE = new TextColor(ChatColor.WHITE.getColor());




    private static final Pattern COLOR_HEX_PATTERN = Pattern.compile("(#[a-fA-F0-9]{6})");
    private static final Pattern COLOR_DEP_PATTERN = Pattern.compile("([a-fA-F0-9])");

    public static Pattern getColorHexPattern(char colorChatCode) {
        return Pattern.compile(colorChatCode + COLOR_HEX_PATTERN.pattern());
    }

    public static Pattern getColorDepPattern(char colorChatCode) {
        return Pattern.compile(colorChatCode + COLOR_DEP_PATTERN.pattern());
    }

    @Getter
    @NotNull
    private final Color color;

    public ChatColor getChatColor() {
        return ChatColor.of(color);
    }

    public static TextColor of(@NotNull String color) {
        Matcher depColorMatcher = COLOR_DEP_PATTERN.matcher(color);
        Matcher hexColorMatcher = COLOR_HEX_PATTERN.matcher(color);
        if (depColorMatcher.matches()) {
            return new TextColor(ChatColor.getByChar(depColorMatcher.group(1).charAt(0)).getColor());
        }
        else if (hexColorMatcher.matches()) {
            return new TextColor(Color.decode(hexColorMatcher.group(1)));
        }
        else throw new IllegalArgumentException(String.format("The string '%s' does not match the color format", color));
    }

    public static TextColor of(@NotNull Color color) {
        return new TextColor(color);
    }

    public static TextColor of(@NotNull ChatColor chatColor) {
        if (Objects.isNull(chatColor.getColor())) throw new IllegalArgumentException("ChatColor object is not a color");
        return new TextColor(chatColor.getColor());
    }


    @Override
    public String getStringDecorator() {
        return getChatColor().toString();
    }
}
