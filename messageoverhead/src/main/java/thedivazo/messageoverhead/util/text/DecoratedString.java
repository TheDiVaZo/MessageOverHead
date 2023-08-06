package thedivazo.messageoverhead.util.text;

import lombok.*;
import org.apache.commons.collections4.iterators.ReverseListIterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import thedivazo.messageoverhead.util.text.decor.TextColor;
import thedivazo.messageoverhead.util.text.decor.TextFormat;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ToString
@EqualsAndHashCode(exclude = {"minecraftColoredString"})
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
                if(chunk.getTextFormats().containsAll(previousChunk.getTextFormats())) {
                    List<TextFormat> uniqueTextFormat = Stream.concat(chunk.getTextFormats().stream(), previousChunk.getTextFormats().stream())
                            .distinct()
                            .filter(f -> !chunk.getTextFormats().contains(f) || !finalPreviousChunk.getTextFormats().contains(f))
                            .collect(Collectors.toList());
                    chunkString.append(uniqueTextFormat.stream().map(TextFormat::getStringDecorator).collect(Collectors.joining("")));
                }
                else {
                    chunkString.append(TextFormat.RESET.getStringDecorator()).append(chunk.getTextFormats().stream().map(TextFormat::getStringDecorator).collect(Collectors.joining("")));
                }
                if (!chunk.getColor().equals(previousChunk.getColor())) chunkString.append(chunk.getColor().getStringDecorator());
            }
            else {
                chunkString.append(chunk.getColor().getStringDecorator());
                chunkString.append(chunk.getTextFormats().stream().map(TextFormat::getStringDecorator).collect(Collectors.joining("")));
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
        return noColorString.charAt(index);
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
        Matcher colorMatcher = TextColor.getPattern().matcher(minecraftColoredString);
        Matcher formatMatcher = TextFormat.getPattern().matcher(minecraftColoredString);

        List<TypeMatchResult> matchResults = Stream.concat(
                colorMatcher.results().map(matchResult -> new TypeMatchResult(matchResult, Type.COLOR)),
                formatMatcher.results().map(matchResult -> new TypeMatchResult(matchResult, Type.FORMAT))
        ).sorted(Comparator.comparingInt(TypeMatchResult::start)).collect(Collectors.toList());


        List<Chunk> newChunks = new ArrayList<>();
        Set<TextFormat> prevTextFormat = new HashSet<>();
        TextColor prevTextColor = TextColor.WHITE;
        int prevStart = 0;

        for (TypeMatchResult matchResult : matchResults) {
            Type type = matchResult.type;
            if(matchResult.start() != prevStart) {
                newChunks.add(Chunk.builder()
                        .setTextFormats(prevTextFormat)
                        .setColor(prevTextColor)
                        .setText(minecraftColoredString.substring(prevStart, matchResult.start()))
                        .build());
            }
            if (type == Type.COLOR) {
                prevStart = matchResult.end();
                prevTextColor = TextColor.of(matchResult.group());
            } else if (type == Type.FORMAT) {
                TextFormat format = TextFormat.of(matchResult.group());
                if (format.equals(TextFormat.RESET)) {
                    prevTextFormat.clear();
                    prevTextColor = TextColor.WHITE;
                } else {
                    prevTextFormat.add(format);
                }
                prevStart = matchResult.end();
            }
        }

        if (prevStart < minecraftColoredString.length()) newChunks.add(Chunk.builder()
                .setColor(prevTextColor)
                .setTextFormats(prevTextFormat)
                .setText(minecraftColoredString.substring(prevStart))
                .build());
        return new DecoratedString(newChunks);
    }

    public static DecoratedString valueOf(@Unmodifiable List<DecoratedChar> decoratedChars) {
        List<Chunk> chunks = new ArrayList<>();
        Chunk.ChunkBuilder chunkBuilderBuffer = Chunk.builder();
        TextColor textColorBuffer = TextColor.WHITE;
        Set<TextFormat> textFormatsBuffer = new HashSet<>();
        StringBuilder textBuffer = new StringBuilder();
        for (DecoratedChar decoratedChar : decoratedChars) {
            TextColor charColor = decoratedChar.getColor();
            Set<TextFormat> charFormats = decoratedChar.getTextFormats();
            if (!charColor.equals(textColorBuffer) || !charFormats.equals(textFormatsBuffer)) {
                Chunk chunk = chunkBuilderBuffer.build();
                if (!chunk.isEmpty())
                    chunks.add(chunk);
                textBuffer.setLength(0);
                textColorBuffer = charColor;
                textFormatsBuffer = charFormats;
                chunkBuilderBuffer = Chunk.builder()
                    .setColor(textColorBuffer)
                    .setTextFormats(textFormatsBuffer);
            }
            textBuffer.append(decoratedChar.getCharWrapped());
            chunkBuilderBuffer.setText(textBuffer.toString());
        }

        Chunk lastChunk = chunkBuilderBuffer.build();
        if (!lastChunk.isEmpty())
            chunks.add(lastChunk);
        return new DecoratedString(chunks);
    }

    @ToString
    enum Type {
        COLOR,
        FORMAT
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
            result.add(Chunk.builder().setText(String.valueOf(newChar.getCharWrapped())).setColor(newChar.getColor()).setTextFormats(newChar.getTextFormats()).build());
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

    public DecoratedString replace(String target, String replacement) {
        return new DecoratedString(chunks.stream().map(chunk -> chunk.replace(target, replacement)).collect(Collectors.toList()));
    }

    public List<Chunk> toChunksList() {
        return new ArrayList<>(chunks);
    }
}
