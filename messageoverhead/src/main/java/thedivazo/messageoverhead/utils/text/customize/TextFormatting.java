package thedivazo.messageoverhead.utils.text.customize;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TextFormatting implements TextChatColorDecorator {

    public static final TextFormatting BOLD = new TextFormatting(ChatColor.BOLD);
    public static final TextFormatting ITALIC = new TextFormatting(ChatColor.ITALIC);
    public static final TextFormatting UNDERLINE = new TextFormatting(ChatColor.UNDERLINE);
    public static final TextFormatting STRIKETHROUGH = new TextFormatting(ChatColor.STRIKETHROUGH);
    public static final TextFormatting MAGIC = new TextFormatting(ChatColor.MAGIC);




    public static final Pattern FORMAT_PATTERN = Pattern.compile("([kmoln])");

    public static Pattern getFormatPattern(char colorChatCode) {
        return Pattern.compile(colorChatCode + FORMAT_PATTERN.pattern());
    }

    @NotNull
    private final ChatColor formatting;

    public static TextFormatting of(String formatting) {
        Matcher formatMatcher = FORMAT_PATTERN.matcher(formatting);
        if (formatMatcher.matches()) {
            return of(ChatColor.getByChar(formatMatcher.group(1).charAt(0)));
        }
        throw new IllegalArgumentException(String.format("The string '%s' does not match the formatting", formatting));
    }

    public static TextFormatting of(ChatColor chatColor) {
        if (ChatColor.BOLD.equals(chatColor)) {
            return BOLD;
        }
        if (ChatColor.ITALIC.equals(chatColor)) {
            return ITALIC;
        }
        if (ChatColor.UNDERLINE.equals(chatColor)) {
            return UNDERLINE;
        }
        if (ChatColor.STRIKETHROUGH.equals(chatColor)) {
            return STRIKETHROUGH;
        }
        if (ChatColor.MAGIC.equals(chatColor)) {
            return MAGIC;
        }
        throw new IllegalArgumentException("ChatColor object is not a format");
    }

    @Override
    public ChatColor getChatColor() {
        return formatting;
    }

    @Override
    public String getStringDecorator() {
        return getChatColor().toString();
    }
}
