package thedivazo.messageoverhead.utils.text.customize;

import lombok.*;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TextOperator implements TextDecorator {

    public static final TextOperator RESET = new TextOperator(ChatColor.RESET.toString());

    @Getter
    public static final Pattern OPERATOR_PATTERN = Pattern.compile("r");

    public static Pattern getOperatorPattern(char colorChatCode) {
        return Pattern.compile(colorChatCode + OPERATOR_PATTERN.pattern());
    }

    private final String operator;

    @Override
    public String getStringDecorator() {
        return operator;
    }

    public static TextOperator of(@NotNull String operator) {
        Matcher operatorMatcher = OPERATOR_PATTERN.matcher(operator);
        if (operatorMatcher.matches()) {
            return RESET;
        }
        else throw new IllegalArgumentException(String.format("The string '%s' does not match the text operator", operator));
    }

    public static TextOperator of(@NotNull ChatColor chatColor) {
        if (!chatColor.equals(ChatColor.RESET)) throw new IllegalArgumentException("ChatColor object is not a color");
        return RESET;
    }
}
