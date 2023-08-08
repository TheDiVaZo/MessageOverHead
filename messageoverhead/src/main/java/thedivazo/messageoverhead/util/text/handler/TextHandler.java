package thedivazo.messageoverhead.util.text.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import thedivazo.messageoverhead.util.text.Chunk;
import thedivazo.messageoverhead.util.text.DecoratedString;
import thedivazo.messageoverhead.util.text.decor.TextColor;
import thedivazo.messageoverhead.util.text.decor.TextFormat;
import thedivazo.messageoverhead.util.text.decor.colorutil.GradientText;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextHandler {

    private enum Type {
        COLOR,
        FORMAT
    }

    @AllArgsConstructor
    @Getter
    private static class TypedMatchResult {
        private MatchResult matchResult;
        private Type type;

        public int start() {
            return getMatchResult().start();
        }

        public int start(int group) {
            return getMatchResult().start(group);
        }

        public int end() {
            return getMatchResult().end();
        }

        public int end(int group) {
            return getMatchResult().end(group);
        }

        public String group() {
            return getMatchResult().group();
        }

        public String group(int group) {
            return getMatchResult().group(group);
        }

        public int groupCount() {
            return getMatchResult().groupCount();
        }
    }

    public static List<BaseComponent> from(String minecraftColoredString) {
        Matcher colorMatcher = TextColor.getPattern().matcher(minecraftColoredString);
        Matcher formatMatcher = TextFormat.getPattern().matcher(minecraftColoredString);

        List<TypedMatchResult> matchResults = Stream.concat(
                colorMatcher.results().map(matchResult -> new TypedMatchResult(matchResult, Type.COLOR)),
                formatMatcher.results().map(matchResult -> new TypedMatchResult(matchResult, Type.FORMAT))
        ).sorted(Comparator.comparingInt(TypedMatchResult::start)).collect(Collectors.toList());

        List<BaseComponent> result = new ArrayList<>();
        int prevStart = 0;

        for (TypedMatchResult matchResult : matchResults) {
            Type type = matchResult.type;
            if(matchResult.start() != prevStart) {
                String text = minecraftColoredString.substring(prevStart, matchResult.start());
                result.add(new TextComponent(text));
            }
            if (type == Type.COLOR) {
                prevStart = matchResult.end();
                TextColor textColor = TextColor.of(matchResult.group());
                result.add(new ColorComponent(textColor));
            } else if (type == Type.FORMAT) {
                prevStart = matchResult.end();
                TextFormat format = TextFormat.of(matchResult.group());
                result.add(new FormatComponent(format));
            }
        }

        if (prevStart < minecraftColoredString.length()) result.add(new TextComponent(minecraftColoredString.substring(prevStart)));

        return result;
    }

    public static List<Chunk> toChunk(List<BaseComponent> baseComponents) {
        List<Chunk> result = new ArrayList<>();
        Set<TextFormat> prevTextFormat = new HashSet<>();
        TextColor prevTextColor = TextColor.WHITE;

        for (BaseComponent baseComponent : baseComponents) {
            if(baseComponent instanceof TextComponent) {
                result.add(Chunk.builder()
                        .setTextFormats(prevTextFormat)
                        .setColor(prevTextColor)
                        .setText(((TextComponent) baseComponent).getText())
                        .build());
            }
            if (baseComponent instanceof ColorComponent) {
                prevTextColor = ((ColorComponent) baseComponent).getColor();
            }
            else if (baseComponent instanceof FormatComponent) {
                TextFormat format = ((FormatComponent) baseComponent).getFormat();
                if (format.equals(TextFormat.RESET)) {
                    prevTextFormat.clear();
                    prevTextColor = TextColor.WHITE;
                } else {
                    prevTextFormat.add(format);
                }
            }
        }
        return result;
    }
}
