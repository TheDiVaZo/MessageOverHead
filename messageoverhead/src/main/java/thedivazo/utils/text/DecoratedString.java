package thedivazo.utils.text;

import lombok.*;
import org.apache.commons.collections4.iterators.ReverseListIterator;
import org.jetbrains.annotations.NotNull;
import thedivazo.utils.text.element.Chunk;
import thedivazo.utils.text.customize.TextColor;
import thedivazo.utils.text.customize.TextFormatting;
import thedivazo.utils.text.customize.TextOperator;
import thedivazo.utils.text.element.DecoratedChar;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ToString
@EqualsAndHashCode
public class DecoratedString implements CharSequence {

    private final List<Chunk> chunks;
    @Getter
    private final String noColorString;
    @Getter
    private final String minecraftColoredString;

    public DecoratedString() {
        this(new ArrayList<>());
    }

    @Builder
    private DecoratedString(@Singular List<Chunk> chunks) {
        this.chunks = chunks;
        this.noColorString = calculateNoColorString();
        this.minecraftColoredString = calculateMinecraftColoredString();
    }

    private String calculateNoColorString() {
        return chunks.stream().map(Chunk::getText).collect(Collectors.joining());
    }

    private String calculateMinecraftColoredString() {
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


    @Override
    public int length() {
        return noColorString.length();
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

    @ToString
    enum Type {
        COLOR,
        FORMAT,
        OPERATOR,
    }

    @ToString
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

    DecoratedString subDecoratedString(int beginIndex, int endIndex) {
        return (DecoratedString) subSequence(beginIndex, endIndex);
    }

    DecoratedString subDecoratedString(int beginIndex) {
        return (DecoratedString) subSequence(beginIndex, length());
    }

    DecoratedString insertColor(int index, TextColor textColor) {
        if (length() >= index) throw  new IndexOutOfBoundsException("You have gone beyond the line boundaries. Index: " + index+ ". Length: " + length());
        List<Chunk> newChunks = new ArrayList<>();
        if(index != 0) {
            newChunks.addAll(subDecoratedString(0, index).chunks);
            List<Chunk> nextChunks = subDecoratedString(index).chunks;
            if(!nextChunks.isEmpty()) nextChunks.set(0, nextChunks.get(0)
                    .toBuilder()
                    .setColor(textColor)
                    .build());
            newChunks.addAll(nextChunks);
        }
        return new DecoratedString(newChunks);
    }

    public int indexOf(String str) {
        return noColorString.indexOf(str);
    }

    public int indexOf(String str, int fromIndex) {
        return noColorString.indexOf(str, fromIndex);
    }

    public int indexOf(DecoratedChar decoratedChar) {
        return indexOf(decoratedChar, 0);
    }

    public int indexOf(DecoratedChar decoratedChar, int fromIndex) {
        if (length() >= fromIndex) return -1;
        int previousLength = 0;
        int charIndex = -1;
        for (Chunk chunk : chunks) {
            int charIndexFromCurrentChunk = chunk.indexOf(decoratedChar);
            if(chunk.length() + previousLength > fromIndex && charIndexFromCurrentChunk != -1 && previousLength+charIndexFromCurrentChunk >= fromIndex ) {
                charIndex = charIndexFromCurrentChunk + previousLength;
                break;
            }
            previousLength += chunk.length();
        }
        return charIndex;
    }

    public int indexOf(DecoratedString decoratedString) {
        return indexOf(decoratedString, 0);
    }
    
    private static int indexOfOneChunk(List<Chunk> chunks, Chunk searchableChunk, int fromIndex) {
        int lastLength = 0;
        for (Chunk chunk : chunks) {
            for (int index = 0, localIndex = chunk.indexOf(searchableChunk, index); localIndex > -1;index += searchableChunk.length(), localIndex = chunk.indexOf(searchableChunk,index)) {
                if (lastLength + localIndex >= fromIndex) {
                    return lastLength + localIndex;
                }
            }
            lastLength += chunk.length();
        }
        return -1;
    }

    private static int indexOfMoreChunks(List<Chunk> chunks, List<Chunk> searchableChunks, int fromIndex) {
        int lastLength = 0;
        Chunk firstChunk = searchableChunks.get(0);
        Chunk lastChunk = searchableChunks.get(searchableChunks.size()-1);
        List<Chunk> middleSearchableChunks = searchableChunks.stream().skip(1).limit(searchableChunks.size()-2).collect(Collectors.toList());
        ListIterator<Chunk> mainChunksIterator = chunks.listIterator();
        while (mainChunksIterator.hasNext()) {
            int chunkIndex = mainChunksIterator.nextIndex();
            Chunk chunk = mainChunksIterator.next();
            int localIndex = chunk.indexOf(firstChunk);
            if (chunkIndex + searchableChunks.size() > chunks.size()) return -1;
            if (chunk.endsWith(firstChunk) && chunks.get(chunkIndex + searchableChunks.size()-1).startsWith(lastChunk) && localIndex != -1 && lastLength + localIndex >= fromIndex) {
                 if (equalsElementsFromChunkIterators(chunks.listIterator(chunkIndex+1), middleSearchableChunks.iterator())) return lastLength + localIndex;
            }
            else lastLength += chunk.length();
        }
        return -1;
    }

    public int indexOf(DecoratedString decoratedString, int fromIndex) {
        if (length() <= fromIndex || decoratedString.length() > length()) return -1;
        else if (decoratedString.chunks.isEmpty()) return fromIndex;
        List<Chunk> searchableChunks = decoratedString.chunks;
        return searchableChunks.size() == 1 ? indexOfOneChunk(chunks, searchableChunks.get(0), fromIndex) : indexOfMoreChunks(chunks, searchableChunks, fromIndex);
    }

    public int lastIndexOf(String str) {
        return noColorString.lastIndexOf(str);
    }

    public int lastIndexOf(String str, int fromIndex) {
        return noColorString.lastIndexOf(str, fromIndex);
    }

    public int lastIndexOf(DecoratedChar decoratedChar) {
        return lastIndexOf(decoratedChar, length());
    }

    public int lastIndexOf(DecoratedChar decoratedChar, int fromIndex) {
        if (fromIndex < 0) return -1;
        int previousLength = length();
        int charIndex = -1;
        for (ReverseListIterator<Chunk> iterator = new ReverseListIterator<>(chunks); iterator.hasNext(); ) {
            Chunk chunk = iterator.next();
            int charIndexFromCurrentChunk = chunk.lastIndexOf(decoratedChar);
            if (previousLength - chunk.length() <= fromIndex && charIndexFromCurrentChunk != -1 && previousLength - (chunk.length() - charIndexFromCurrentChunk) <= fromIndex) {
                charIndex = previousLength - (chunk.length() - charIndexFromCurrentChunk);
                break;
            }
            previousLength += chunk.length();
        }
        return charIndex;
    }

    public int lastIndexOf(DecoratedString decoratedString) {
        return lastIndexOf(decoratedString, 0);
    }

    private static int lastIndexOfOneChunk(List<Chunk> chunks, Chunk searchableChunk, int fromIndex) {
        int lastLength = chunks.stream().mapToInt(Chunk::length).sum();
        for (ReverseListIterator<Chunk> iterator = new ReverseListIterator<>(chunks); iterator.hasNext(); ) {
            Chunk chunk = iterator.next();
            for (int index = chunk.length(), localIndex = chunk.lastIndexOf(searchableChunk, index); localIndex > -1; index-=searchableChunk.length(), localIndex = chunk.lastIndexOf(searchableChunk, index)) {
                if (lastLength - (chunk.length() - localIndex) <= fromIndex) {
                    return lastLength - (chunk.length() - localIndex);
                }
            }
            lastLength -= chunk.length();
        }
        return -1;
    }

    private static int lastIndexOfMoreChunks(List<Chunk> chunks, List<Chunk> searchableChunks, int fromIndex) {
        int lastLength = chunks.stream().mapToInt(Chunk::length).sum();
        Chunk firstChunk = searchableChunks.get(0);
        Chunk lastChunk = searchableChunks.get(searchableChunks.size()-1);
        List<Chunk> middleSearchableChunks = searchableChunks.stream().skip(1).limit(searchableChunks.size()-2).collect(Collectors.toList());
        ListIterator<Chunk> mainChunksIterator = new ReverseListIterator<>(chunks);
        while (mainChunksIterator.hasNext()) {
            int chunkIndex = mainChunksIterator.nextIndex();
            Chunk chunk = mainChunksIterator.next();
            int localIndex = chunk.lastIndexOf(lastChunk);
            if (searchableChunks.size() - chunkIndex < 0) return -1;
            if (
                    chunk.startsWith(lastChunk) &&
                    chunks.get(searchableChunks.size()-chunkIndex).endsWith(firstChunk) &&
                    localIndex != -1 &&
                    lastLength - (chunk.length() - localIndex) <= fromIndex &&
                    equalsElementsFromChunkIterators(chunks.listIterator(chunkIndex+1), middleSearchableChunks.iterator())
            )
                return lastLength - (chunk.length() - localIndex);
            else lastLength -= chunk.length();
        }
        return -1;
    }

    public int lastIndexOf(DecoratedString decoratedString, int fromIndex) {
        if (fromIndex < 0) return -1;
        else if (decoratedString.chunks.isEmpty()) return fromIndex;
        List<Chunk> searchableChunks = decoratedString.chunks;
        return searchableChunks.size() == 1 ? lastIndexOfOneChunk(chunks, searchableChunks.get(0), fromIndex) : lastIndexOfMoreChunks(chunks, searchableChunks, fromIndex);
    }

    private static boolean equalsElementsFromChunkIterators(Iterator<Chunk> intermediateChunksIterator, Iterator<Chunk> middleSearchableChunksIterator) {
        boolean flag = true;
        while (intermediateChunksIterator.hasNext() && middleSearchableChunksIterator.hasNext()) {
            if(!intermediateChunksIterator.next().equals(middleSearchableChunksIterator.next())) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    public boolean contains(CharSequence s) {
        return indexOf(s.toString()) > -1;
    }

    public boolean contains(String str) {
        return indexOf(str) > -1;
    }

    public boolean contains(DecoratedString decoratedString) {
        return indexOf(decoratedString) > -1;
    }

    public boolean contains(DecoratedChar decoratedChar) {
        return indexOf(decoratedChar) > -1;
    }

    public boolean isEmpty() {
        return chunks.isEmpty() || chunks.stream().allMatch(Chunk::isEmpty);
    }


    public DecoratedString replace(char oldChar, char newChar) {
        return new DecoratedString(chunks.stream().map(chunk -> chunk.toBuilder().setText(chunk.getText().replace(oldChar, newChar)).build()).collect(Collectors.toList()));
    }

    private static List<Chunk> replaceInChunk(Chunk chunk, DecoratedChar oldChar, DecoratedChar newChar) {
        if (!chunk.equalsDecorate(oldChar)) return new ArrayList<>() {{
            add(chunk);
        }};
        List<Chunk> result = new ArrayList<>();

        Matcher matcher = Pattern.compile(Pattern.quote(String.valueOf(oldChar.getCharWrapped()))).matcher(chunk.getText());
        int lastIndex = 0;
        while (matcher.find()) {
            MatchResult matchResult = matcher.toMatchResult();
            if (lastIndex != matchResult.start())
                result.add(chunk.toBuilder().setText(chunk.getText().substring(lastIndex, matchResult.start())).build());
            result.add(Chunk.builder().setText(String.valueOf(newChar.getCharWrapped())).setColor(newChar.getColor()).setTextFormatting(newChar.getTextFormatting()).build());
            lastIndex = matchResult.end();
        }
        if (lastIndex != chunk.getText().length() - 1)
            result.add(chunk.toBuilder().setText(chunk.getText().substring(lastIndex)).build());
        return result;
    }

    public DecoratedString replace(DecoratedChar oldChar, DecoratedChar newChar) {
        List<Chunk> newChunks = new ArrayList<>();
        for (Chunk chunk : chunks) {
            if (chunk.contains(oldChar)) {
                List<Chunk> replacementChunk = replaceInChunk(chunk, oldChar, newChar);
                newChunks.addAll(replacementChunk);
            }
            else newChunks.add(chunk);
        }
        return new DecoratedString(newChunks);
    }

    public DecoratedString replace(String target, DecoratedString replacement) {
        List<Chunk> newChunks = new ArrayList<>();
        int fromIndex = 0;
        for (int startIndex = indexOf(target, fromIndex); startIndex > -1; startIndex = indexOf(target, fromIndex)) {
            int endIndex = target.length() + startIndex;
            if (fromIndex != startIndex)
                newChunks.addAll(subDecoratedString(fromIndex, startIndex).chunks);
            newChunks.addAll(replacement.chunks);
            fromIndex = endIndex;
        }
        if (fromIndex < length()) newChunks.addAll(subDecoratedString(fromIndex).chunks);
        return new DecoratedString(newChunks);
    }

    public DecoratedString replace(DecoratedString target, DecoratedString replacement) {
        List<Chunk> newChunks = new ArrayList<>();
        int fromIndex = 0;
        for (int startIndex = indexOf(target, fromIndex); startIndex > -1; startIndex = indexOf(target, fromIndex)) {
            int endIndex = target.length() + startIndex;
            if (fromIndex != startIndex)
                newChunks.addAll(subDecoratedString(fromIndex, startIndex).chunks);
            newChunks.addAll(replacement.chunks);
            fromIndex = endIndex;
        }
        if (fromIndex < length()) newChunks.addAll(subDecoratedString(fromIndex).chunks);
        return new DecoratedString(newChunks);
    }

    public List<Chunk> toChunksList() {
        return new ArrayList<>(chunks);
    }
}
