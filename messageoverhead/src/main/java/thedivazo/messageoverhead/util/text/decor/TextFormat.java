package thedivazo.messageoverhead.util.text.decor;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ToString
@EqualsAndHashCode
public class TextFormat implements TextDecorator {
    public static final TextFormat BOLD = new TextFormat(ChatColor.BOLD);
    public static final TextFormat ITALIC = new TextFormat(ChatColor.ITALIC);
    public static final TextFormat UNDERLINE = new TextFormat(ChatColor.UNDERLINE);
    public static final TextFormat STRIKETHROUGH = new TextFormat(ChatColor.STRIKETHROUGH);
    public static final TextFormat MAGIC = new TextFormat(ChatColor.MAGIC);
    public static final TextFormat RESET = new TextFormat(ChatColor.RESET);

    private static final Pattern pattern = Pattern.compile("(&[kmolnr])");

    public static TextFormat of(ChatColor format) {
        if (ChatColor.BOLD.equals(format)) return BOLD;
        else if (ChatColor.ITALIC.equals(format)) return ITALIC;
        else if (ChatColor.UNDERLINE.equals(format)) return UNDERLINE;
        else if (ChatColor.STRIKETHROUGH.equals(format)) return STRIKETHROUGH;
        else if (ChatColor.MAGIC.equals(format)) return MAGIC;
        else if (ChatColor.RESET.equals(format)) return RESET;
        else throw new IllegalArgumentException("The ChatColor argument must be a format, not a color");
    }

    public static TextFormat of(String format) {
        Matcher matcher = pattern.matcher(format);
        if (matcher.matches()) {
            char formatChat = matcher.group().charAt(1);
            switch (formatChat) {
                case 'l':
                    return BOLD;
                case 'o':
                    return ITALIC;
                case 'n':
                    return UNDERLINE;
                case 'm':
                    return STRIKETHROUGH;
                case 'k':
                    return MAGIC;
                case 'r':
                    return RESET;
            }
        }
        throw new IllegalArgumentException("The string '" + format + "' does not match the pattern '" + pattern.pattern() + "'");
    }

    private final ChatColor format;

    private TextFormat(ChatColor format) {
        this.format = format;
    }

    @Override
    public ChatColor getChatColor() {
        return format;
    }

    @Override
    public String getStringDecorator() {
        return format.toString();
    }

    public static Pattern getPattern() {
        return pattern;
    }
}
