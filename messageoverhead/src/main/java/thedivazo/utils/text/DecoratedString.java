package thedivazo.utils.text;

import com.google.common.collect.Collections2;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Singular;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import thedivazo.utils.text.chunk.Chunk;
import thedivazo.utils.text.customize.TextColor;
import thedivazo.utils.text.customize.TextFormatting;
import thedivazo.utils.text.customize.TextOperator;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
public class DecoratedString implements CharSequence {
    @Singular
    private final List<Chunk> chunks;


    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt(int index) {
        return 0;
    }

    @NotNull
    @Override
    public CharSequence subSequence(int start, int end) {
        List<Chunk> trimmedChunks = new ArrayList<>();

        int remainingCount = end-start;

        for (Chunk chunk : chunks) {
            int chunkSize = chunk.length();

            if (start < chunkSize) {
                int curEnd = Math.min(start + remainingCount, chunkSize);
                Chunk trimmedSubChunk = (Chunk) chunk.subSequence(start, curEnd);
                trimmedChunks.add(trimmedSubChunk);
                remainingCount -= (curEnd - start);
                if (remainingCount <= 0) {
                    break;
                }
            }

            start = Math.max(start - chunkSize, 0);
        }

        return new DecoratedString(trimmedChunks);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static DecoratedString contact(DecoratedString s1, DecoratedString s2) {
        List<Chunk> commonChunks = Stream.concat(s1.chunks.stream(), s2.chunks.stream()).collect(Collectors.toList());
        return new DecoratedString(commonChunks);
    }

    public String toMinecraftColoredString() {
        StringBuilder result = new StringBuilder();
        ListIterator<Chunk> chunkListIterator = chunks.listIterator();
        Chunk previousChunk = null;
        while (chunkListIterator.hasNext()) {
            StringBuilder chunkString = new StringBuilder();
            Chunk chunk = chunkListIterator.next();
            if(!Objects.isNull(previousChunk)) {
                Chunk finalPreviousChunk = previousChunk;
                if(new HashSet<>(chunk.getTextFormatting()).containsAll(previousChunk.getTextFormatting())) {
                    List<TextFormatting> uniqueTextFormatting = Stream.concat(chunk.getTextFormatting().stream(), previousChunk.getTextFormatting().stream())
                            .distinct()
                            .filter(f -> !chunk.getTextFormatting().contains(f) || !finalPreviousChunk.getTextFormatting().contains(f))
                            .collect(Collectors.toList());
                    chunkString.append(uniqueTextFormatting.stream().map(TextFormatting::getStringDecorator).collect(Collectors.joining("")));
                }
                else {
                    chunkString.append(TextOperator.RESET.getStringDecorator()).append(chunk.getTextFormatting().stream().map(TextFormatting::getStringDecorator).collect(Collectors.joining("")));
                }
                if (!chunk.getColor().equals(previousChunk.getColor())) chunkString.append(chunk.getColor().getStringDecorator());
            }
            else {
                chunkString.append(chunk.getColor().getStringDecorator());
                chunkString.append(chunk.getTextFormatting().stream().map(TextFormatting::getStringDecorator).collect(Collectors.joining("")));

            }
            chunkString.append(chunk.getText());
            result.append(chunkString);
            previousChunk = chunk;
        }
        return result.toString();
    }

    public static DecoratedString valueOf(String minecraftColoredString) {
        char colorChar = '&';

        Matcher colorHexMatcher = TextColor.getColorHexPattern(colorChar).matcher(minecraftColoredString);
        Matcher colorDepMatcher = TextColor.getColorDepPattern(colorChar).matcher(minecraftColoredString);

        Matcher formatMatcher = TextFormatting.getFormatPattern(colorChar).matcher(minecraftColoredString);

        Matcher operatorMatcher = TextOperator.getOperatorPattern(colorChar).matcher(minecraftColoredString);

        List<TypeMatchResult> matchResults = new ArrayList<>();

        while (true) {
            if (colorHexMatcher.find())
                matchResults.add(new TypeMatchResult(colorHexMatcher.toMatchResult(), Type.COLOR));
            else if (colorDepMatcher.find())
                matchResults.add(new TypeMatchResult(colorDepMatcher.toMatchResult(), Type.COLOR));
            else if (formatMatcher.find())
                matchResults.add(new TypeMatchResult(formatMatcher.toMatchResult(), Type.FORMAT));
            else if (operatorMatcher.find())
                matchResults.add(new TypeMatchResult(operatorMatcher.toMatchResult(), Type.OPERATOR));
            else break;
        }
        matchResults.sort(Comparator.comparingInt(TypeMatchResult::start));

        List<Chunk> newChunks = new ArrayList<>();

        Set<TextFormatting> prevTextFormatting = new HashSet<>();
        TextColor prevTextColor = TextColor.WHITE;
        int prevStart = 0;

        for (TypeMatchResult matchResult : matchResults) {
            Type type = matchResult.type;
            if(matchResult.start() != prevStart) {
                newChunks.add(Chunk.builder()
                        .setTextFormatting(prevTextFormatting)
                        .setColor(prevTextColor)
                        .setText(minecraftColoredString.substring(prevStart, matchResult.start()))
                        .build());
            }
            switch (type) {
                case COLOR: {
                    prevStart = matchResult.end();
                    prevTextColor = TextColor.of(matchResult.group(1));
                    break;
                }
                case FORMAT: {
                    prevStart = matchResult.end();
                    prevTextFormatting.add(TextFormatting.of(matchResult.group(1)));
                    break;
                }
                case OPERATOR: {
                    prevTextFormatting.clear();
                    prevTextColor = TextColor.WHITE;
                    prevStart = matchResult.end();
                    break;
                }
            }
        }

        if (prevStart < minecraftColoredString.length()) newChunks.add(Chunk.builder()
                .setColor(prevTextColor)
                .setTextFormatting(prevTextFormatting)
                .setText(minecraftColoredString.substring(prevStart))
                .build());
        return new DecoratedString(newChunks);
    }

    enum Type {
        COLOR,
        FORMAT,
        OPERATOR,
    }

    @AllArgsConstructor
    static class TypeMatchResult {
        private final MatchResult matchResult;
        private final Type type;

        public int start() {
            return matchResult.start();
        }

        public int start(int group) {
            return matchResult.start(group);
        }

        public int end() {
            return matchResult.end();
        }

        public int end(int group) {
            return matchResult.end(group);
        }

        public String group() {
            return matchResult.group();
        }

        public String group(int group) {
            return matchResult.group(group);
        }

        public int groupCount() {
            return matchResult.groupCount();
        }
    }
}
