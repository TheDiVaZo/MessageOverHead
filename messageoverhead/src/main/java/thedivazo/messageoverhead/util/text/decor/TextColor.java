package thedivazo.messageoverhead.util.text.decor;

import lombok.ToString;
import net.md_5.bungee.api.ChatColor;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.util.VersionWrapper;

import java.awt.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ToString
public class TextColor implements TextDecorator {
    public static final TextColor BLACK = new TextColor(ChatColor.BLACK);
    public static final TextColor DARK_BLUE = new TextColor(ChatColor.DARK_BLUE);
    public static final TextColor DARK_GREEN = new TextColor(ChatColor.DARK_GREEN);
    public static final TextColor DARK_AQUA = new TextColor(ChatColor.DARK_AQUA);
    public static final TextColor DARK_RED = new TextColor(ChatColor.DARK_RED);
    public static final TextColor DARK_PURPLE = new TextColor(ChatColor.DARK_PURPLE);
    public static final TextColor GOLD = new TextColor(ChatColor.GOLD);
    public static final TextColor GRAY = new TextColor(ChatColor.GRAY);
    public static final TextColor DARK_GRAY = new TextColor(ChatColor.DARK_GRAY);
    public static final TextColor BLUE = new TextColor(ChatColor.BLUE);
    public static final TextColor GREEN = new TextColor(ChatColor.GREEN);
    public static final TextColor AQUA = new TextColor(ChatColor.AQUA);
    public static final TextColor RED = new TextColor(ChatColor.RED);
    public static final TextColor LIGHT_PURPLE = new TextColor(ChatColor.LIGHT_PURPLE);
    public static final TextColor YELLOW = new TextColor(ChatColor.YELLOW);
    public static final TextColor WHITE = new TextColor(ChatColor.WHITE);

    private static final Pattern HEX_PATTERN = Pattern.compile("(&#[0-9a-fA-F]{6})");
    private static final Pattern USUAL_PATTERN = Pattern.compile("(&[0-9a-f])");
    private static final Pattern PATTERN = Pattern.compile(HEX_PATTERN.pattern() + "|" + USUAL_PATTERN.pattern());

    private static final VersionWrapper MC_1_16 = VersionWrapper.valueOf("1.16");

    public static TextColor of(Color color) {
        return new TextColor(color);
    }


    public static TextColor of(String color) {
        Matcher hexMatcher = HEX_PATTERN.matcher(color);
        Matcher usualMatcher = USUAL_PATTERN.matcher(color);
        if (hexMatcher.matches()) {
            return new TextColor(Color.decode(hexMatcher.group().substring(1)));
        } else if (usualMatcher.matches()) {
            return new TextColor(ChatColor.getByChar(usualMatcher.group().charAt(1)));
        } else
            throw new IllegalArgumentException("The string '" + color + "' does not match the pattern '" + PATTERN.pattern() + "'");
    }

    private final ChatColor color;

    private TextColor(Color color) {
        this.color = ChatColor.of(color);
    }

    private TextColor(ChatColor chatColor) {
        if (chatColor.getColor() == null)
            throw new IllegalArgumentException("ChatColor should be a color, not a format");
        this.color = chatColor;
    }

    @Override
    public ChatColor getChatColor() {
        return color;
    }

    public static Pattern getPATTERN() {
        if (MessageOverHead.MINECRAFT_VERSION.greater(MC_1_16)) return PATTERN;
        else return USUAL_PATTERN;
    }

    @Override
    public String getStringDecorator() {
        return color.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextColor)) return false;
        TextColor textColor = (TextColor) o;
        return color.getColor().equals(textColor.color.getColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(color.getColor());
    }
}
