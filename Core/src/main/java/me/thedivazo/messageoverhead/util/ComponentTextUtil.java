package me.thedivazo.messageoverhead.util;

import kotlin.collections.CollectionsKt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ComponentTextUtil {
    private ComponentTextUtil() {
    }

    public static Component join(List<Component> list) {
        Objects.requireNonNull(list, "list");
        if (list.isEmpty()) {
            return Component.empty();
        }
        return CollectionsKt.reduce(list, (first, second) -> first.append(Component.newline()).append(second));
    }

    public static List<Component> split(Component component) {
        if (component == null) {
            throw new IllegalArgumentException("component cannot be null");
        }

        List<Component> lines = new ArrayList<>();
        ComponentLine currentLine = new ComponentLine();

        appendSplit(component, Style.empty(), currentLine, lines);
        lines.add(currentLine.build());
        return lines;
    }

    public static List<Component> wrapMessage(Component message, int maxLineSize, int maxWordSize) {
        Objects.requireNonNull(message, "message");

        if (maxLineSize <= 0) {
            throw new IllegalArgumentException(
                    "maxLineSize должен быть больше 0"
            );
        }

        if (maxWordSize <= 0) {
            throw new IllegalArgumentException(
                    "maxWordSize должен быть больше 0"
            );
        }

        List<ComponentWord> words = extractWords(message);
        List<Component> lines = new ArrayList<>();

        if (words.isEmpty()) {
            return lines;
        }

        int wordPartSize = Math.min(maxWordSize, maxLineSize);
        ComponentLine currentLine = new ComponentLine();

        for (ComponentWord word : words) {
            if (word.length() > wordPartSize) {
                if (!currentLine.isEmpty()) {
                    lines.add(currentLine.build());
                    currentLine.clear();
                }

                int position = 0;

                while (word.length() - position >= wordPartSize) {
                    lines.add(word.slice(position, position + wordPartSize).build());
                    position += wordPartSize;
                }

                if (position < word.length()) {
                    currentLine.append(word.slice(position, word.length()));
                }

                continue;
            }

            int newLineSize = currentLine.isEmpty()
                    ? word.length()
                    : currentLine.length() + 1 + word.length();

            if (newLineSize <= maxLineSize) {
                if (!currentLine.isEmpty()) {
                    currentLine.appendSpace();
                }

                currentLine.append(word);
            } else {
                if (!currentLine.isEmpty()) {
                    lines.add(currentLine.build());
                }

                currentLine.clear();
                currentLine.append(word);
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.build());
        }

        return lines;
    }

    private static List<ComponentWord> extractWords(Component message) {
        List<ComponentWord> words = new ArrayList<>();
        ComponentWordBuilder currentWord = new ComponentWordBuilder();
        appendWords(message, Style.empty(), currentWord, words);
        currentWord.flush(words);
        return words;
    }

    private static void appendWords(
            Component component,
            Style parentStyle,
            ComponentWordBuilder currentWord,
            List<ComponentWord> words
    ) {
        Style style = parentStyle.merge(component.style(), Style.Merge.Strategy.ALWAYS);

        if (component instanceof TextComponent textComponent) {
            appendContent(textComponent.content(), style, currentWord, words);
        }

        for (Component child : component.children()) {
            appendWords(child, style, currentWord, words);
        }
    }

    private static void appendSplit(
            Component component,
            Style parentStyle,
            ComponentLine currentLine,
            List<Component> lines
    ) {
        Style style = parentStyle.merge(component.style(), Style.Merge.Strategy.ALWAYS);

        if (component instanceof TextComponent textComponent) {
            appendSplitContent(textComponent.content(), style, currentLine, lines);
        }

        for (Component child : component.children()) {
            appendSplit(child, style, currentLine, lines);
        }
    }

    private static void appendSplitContent(
            String content,
            Style style,
            ComponentLine currentLine,
            List<Component> lines
    ) {
        int start = 0;

        for (int index = 0; index < content.length(); index++) {
            if (content.charAt(index) != '\n') {
                continue;
            }

            if (start < index) {
                currentLine.append(text(content.substring(start, index), style), index - start);
            }

            lines.add(currentLine.build());
            currentLine.clear();
            start = index + 1;
        }

        if (start < content.length()) {
            currentLine.append(text(content.substring(start), style), content.length() - start);
        }
    }

    private static Component text(String content, Style style) {
        return Component.text()
                .content(content)
                .style(style)
                .build();
    }

    private static void appendContent(
            String content,
            Style style,
            ComponentWordBuilder currentWord,
            List<ComponentWord> words
    ) {
        int start = -1;

        for (int index = 0; index < content.length(); index++) {
            if (Character.isWhitespace(content.charAt(index))) {
                if (start != -1) {
                    currentWord.append(content.substring(start, index), style);
                    start = -1;
                }

                currentWord.flush(words);
                continue;
            }

            if (start == -1) {
                start = index;
            }
        }

        if (start != -1) {
            currentWord.append(content.substring(start), style);
        }
    }

    private record TextPart(String content, Style style) {
        private Component build() {
            return Component.text()
                    .content(content)
                    .style(style)
                    .build();
        }
    }

    private record ComponentWord(List<TextPart> parts, int length) {
        private ComponentWord {
            parts = List.copyOf(parts);
        }

        private ComponentWord slice(int start, int end) {
            List<TextPart> slicedParts = new ArrayList<>();
            int offset = 0;

            for (TextPart part : parts) {
                int partEnd = offset + part.content().length();

                if (partEnd <= start) {
                    offset = partEnd;
                    continue;
                }

                if (offset >= end) {
                    break;
                }

                int from = Math.max(start - offset, 0);
                int to = Math.min(end - offset, part.content().length());
                slicedParts.add(new TextPart(part.content().substring(from, to), part.style()));
                offset = partEnd;
            }

            return new ComponentWord(slicedParts, end - start);
        }

        private Component build() {
            Component component = Component.empty();

            for (TextPart part : parts) {
                component = component.append(part.build());
            }

            return component;
        }
    }

    private static final class ComponentWordBuilder {
        private final List<TextPart> parts = new ArrayList<>();
        private int length;

        private void append(String content, Style style) {
            if (content.isEmpty()) {
                return;
            }

            parts.add(new TextPart(content, style));
            length += content.length();
        }

        private void flush(List<ComponentWord> words) {
            if (parts.isEmpty()) {
                return;
            }

            words.add(new ComponentWord(parts, length));
            parts.clear();
            length = 0;
        }
    }

    private static final class ComponentLine {
        private final List<Component> parts = new ArrayList<>();
        private int length;

        private boolean isEmpty() {
            return parts.isEmpty();
        }

        private int length() {
            return length;
        }

        private void appendSpace() {
            parts.add(Component.space());
            length++;
        }

        private void append(ComponentWord word) {
            parts.add(word.build());
            length += word.length();
        }

        private void append(Component component, int length) {
            parts.add(component);
            this.length += length;
        }

        private void clear() {
            parts.clear();
            length = 0;
        }

        private Component build() {
            Component component = Component.empty();

            for (Component part : parts) {
                component = component.append(part);
            }

            return component;
        }
    }
}
