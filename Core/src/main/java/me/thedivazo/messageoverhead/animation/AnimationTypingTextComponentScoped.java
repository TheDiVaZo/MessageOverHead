package me.thedivazo.messageoverhead.animation;

import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.component.TextComponent;
import me.thedivazo.messageoverhead.core.component.scope.ComponentScoped;
import me.thedivazo.messageoverhead.core.component.scope.ScopedFactory;
import me.thedivazo.messageoverhead.core.render.capability.TextCapability;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class AnimationTypingTextComponentScoped implements ComponentScoped<TextComponent.TextState> {
    public static final String DEFAULT_SCOPED_ID = "typing-text-animation";
    public static final Settings DEFAULT_SETTINGS = new Settings(1, 0, 0, true, null, 0);

    private @Nullable ActiveBubble activeBubble;
    private List<AnimatedLine> lines = List.of();
    private List<Component> fullLines = List.of();
    private Settings settings;
    private int totalCharacters;

    private int elapsedTicks;
    private int visibleCharacters;
    private boolean complete;

    public AnimationTypingTextComponentScoped() {
        this(DEFAULT_SETTINGS);
    }

    public AnimationTypingTextComponentScoped(Settings settings) {
        this.settings = Objects.requireNonNull(settings, "settings");
    }

    public static ScopedFactory<TextComponent.TextState> factory() {
        return factory(DEFAULT_SETTINGS);
    }

    public static ScopedFactory<TextComponent.TextState> factory(Settings settings) {
        Objects.requireNonNull(settings, "settings");
        return ignored -> new AnimationTypingTextComponentScoped(settings);
    }

    public static @Nullable AnimationTypingTextComponentScoped getOrAttach(ActiveBubble bubble) {
        return getOrAttach(bubble, DEFAULT_SETTINGS);
    }

    public static @Nullable AnimationTypingTextComponentScoped getOrAttach(
            ActiveBubble bubble,
            Settings settings
    ) {
        Objects.requireNonNull(bubble, "bubble");
        Objects.requireNonNull(settings, "settings");

        TextComponent textComponent = TextComponent.getOrAttach(bubble);
        if (textComponent == null) {
            return null;
        }

        ComponentScoped<TextComponent.TextState> scoped = textComponent.get(DEFAULT_SCOPED_ID);
        if (scoped instanceof AnimationTypingTextComponentScoped animation) {
            animation.setSettings(settings);
            animation.restart();
            return animation;
        }

        ComponentScoped<TextComponent.TextState> attached = textComponent.attach(
                DEFAULT_SCOPED_ID,
                factory(settings)
        );
        if (attached instanceof AnimationTypingTextComponentScoped animation) {
            return animation;
        }
        return null;
    }

    @Override
    public void onAttached(ActiveBubble activeBubble) {
        this.activeBubble = Objects.requireNonNull(activeBubble, "activeBubble");
        captureText(activeBubble);
        applyInitialText();
    }

    public Settings settings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = Objects.requireNonNull(settings, "settings");
    }

    public List<Component> fullLines() {
        return fullLines;
    }

    public List<Component> getFullLines() {
        return fullLines();
    }

    public int visibleCharacters() {
        return visibleCharacters;
    }

    public int totalCharacters() {
        return totalCharacters;
    }

    public boolean isComplete() {
        return complete;
    }

    public void restart() {
        elapsedTicks = 0;
        visibleCharacters = 0;
        complete = false;
        applyInitialText();
    }

    public void restartFromActiveBubble() {
        ActiveBubble bubble = requireAttachedBubble();
        captureText(bubble);
        applyInitialText();
    }

    public void finish(TextComponent.TextState context) {
        Objects.requireNonNull(context, "context");
        elapsedTicks = totalAnimationTicks();
        visibleCharacters = totalCharacters;
        complete = true;
        context.setLines(fullLines);
    }

    @Override
    public void onTick(TextComponent.TextState context) {
        Objects.requireNonNull(context, "context");

        if (complete) {
            context.setLines(fullLines);
            return;
        }

        elapsedTicks++;
        RenderedText renderedText = render();
        visibleCharacters = renderedText.visibleCharacters();
        complete = renderedText.complete();
        context.setLines(renderedText.lines());
    }

    private void captureText(ActiveBubble bubble) {
        TextCapability textCapability = bubble.capabilities().capabilityOrNull(TextCapability.class);
        List<Component> sourceLines = textCapability != null
                ? textCapability.getLines()
                : bubble.message().lines();

        List<AnimatedLine> animatedLines = new ArrayList<>(sourceLines.size());
        List<Component> fullLines = new ArrayList<>(sourceLines.size());
        int characterCount = 0;

        for (int i = 0; i < sourceLines.size(); i++) {
            Component line = Objects.requireNonNull(sourceLines.get(i), "sourceLines[" + i + "]");
            AnimatedLine animatedLine = AnimatedLine.from(line);
            animatedLines.add(animatedLine);
            fullLines.add(line);
            characterCount += animatedLine.length();
        }

        this.lines = List.copyOf(animatedLines);
        this.fullLines = List.copyOf(fullLines);
        this.totalCharacters = characterCount;
        elapsedTicks = 0;
        visibleCharacters = 0;
        complete = false;
    }

    private void applyInitialText() {
        ActiveBubble bubble = activeBubble;
        if (bubble == null) {
            return;
        }

        TextCapability textCapability = bubble.capabilities().capabilityOrNull(TextCapability.class);
        if (textCapability == null) {
            return;
        }

        textCapability.setLines(render().lines());
    }

    private ActiveBubble requireAttachedBubble() {
        if (activeBubble == null) {
            throw new IllegalStateException("Animation is not attached to an active bubble");
        }
        return activeBubble;
    }

    private RenderedText render() {
        if (lines.isEmpty()) {
            return new RenderedText(List.of(), 0, true);
        }

        int remainingTicks = elapsedTicks - settings.startDelayTicks();
        if (remainingTicks <= 0) {
            return new RenderedText(
                    settings.clearBeforeStart() ? initialLines() : fullLines,
                    0,
                    false
            );
        }

        List<Component> visibleLines = new ArrayList<>();
        int visibleCharacterCount = 0;

        for (int i = 0; i < lines.size(); i++) {
            AnimatedLine line = lines.get(i);
            int visibleInLine = Math.min(line.length(), remainingTicks / settings.ticksPerCharacter());

            if (visibleInLine < line.length()) {
                visibleCharacterCount += visibleInLine;
                visibleLines.add(withCursor(line.slice(visibleInLine)));
                return new RenderedText(visibleLines, visibleCharacterCount, false);
            }

            visibleCharacterCount += line.length();
            visibleLines.add(fullLines.get(i));
            remainingTicks -= line.length() * settings.ticksPerCharacter();

            if (i < lines.size() - 1) {
                if (remainingTicks < settings.lineDelayTicks()) {
                    replaceLastLineWithCursor(visibleLines);
                    return new RenderedText(visibleLines, visibleCharacterCount, false);
                }
                remainingTicks -= settings.lineDelayTicks();
            }
        }

        return new RenderedText(fullLines, totalCharacters, true);
    }

    private List<Component> initialLines() {
        Component cursor = visibleCursor();
        return cursor == null ? List.of() : List.of(cursor);
    }

    private Component withCursor(Component line) {
        Component cursor = visibleCursor();
        if (cursor == null) {
            return line;
        }
        return Component.empty().equals(line) ? cursor : line.append(cursor);
    }

    private void replaceLastLineWithCursor(List<Component> visibleLines) {
        Component cursor = visibleCursor();
        if (cursor == null || visibleLines.isEmpty()) {
            return;
        }
        int lastIndex = visibleLines.size() - 1;
        visibleLines.set(lastIndex, visibleLines.get(lastIndex).append(cursor));
    }

    private @Nullable Component visibleCursor() {
        Component cursor = settings.cursor();
        if (cursor == null) {
            return null;
        }

        int blinkIntervalTicks = settings.cursorBlinkIntervalTicks();
        if (blinkIntervalTicks <= 0) {
            return cursor;
        }

        int blinkStep = Math.max(elapsedTicks - 1, 0) / blinkIntervalTicks;
        return blinkStep % 2 == 0 ? cursor : null;
    }

    private int totalAnimationTicks() {
        if (lines.isEmpty()) {
            return settings.startDelayTicks();
        }

        return settings.startDelayTicks()
                + totalCharacters * settings.ticksPerCharacter()
                + (lines.size() - 1) * settings.lineDelayTicks();
    }

    public record Settings(
            int ticksPerCharacter,
            int startDelayTicks,
            int lineDelayTicks,
            boolean clearBeforeStart,
            @Nullable Component cursor,
            int cursorBlinkIntervalTicks
    ) {
        public Settings(int ticksPerCharacter) {
            this(ticksPerCharacter, 0, 0, true, null, 0);
        }

        public Settings {
            if (ticksPerCharacter <= 0) {
                throw new IllegalArgumentException("ticksPerCharacter must be positive");
            }
            if (startDelayTicks < 0) {
                throw new IllegalArgumentException("startDelayTicks must be non-negative");
            }
            if (lineDelayTicks < 0) {
                throw new IllegalArgumentException("lineDelayTicks must be non-negative");
            }
            if (cursorBlinkIntervalTicks < 0) {
                throw new IllegalArgumentException("cursorBlinkIntervalTicks must be non-negative");
            }
        }

        public static Settings defaults() {
            return DEFAULT_SETTINGS;
        }
    }

    private record RenderedText(List<Component> lines, int visibleCharacters, boolean complete) {
        private RenderedText {
            lines = List.copyOf(lines);
        }
    }

    private record AnimatedLine(Component fullLine, List<TextPart> parts, int length) {
        private AnimatedLine {
            parts = List.copyOf(parts);
        }

        private static AnimatedLine from(Component line) {
            List<TextPart> parts = new ArrayList<>();
            appendParts(line, Style.empty(), parts);

            int length = 0;
            for (TextPart part : parts) {
                length += part.length();
            }

            return new AnimatedLine(line, parts, length);
        }

        private Component slice(int length) {
            if (length <= 0) {
                return Component.empty();
            }
            if (length >= this.length) {
                return fullLine;
            }

            Component result = null;
            int remaining = length;

            for (TextPart part : parts) {
                if (remaining <= 0) {
                    break;
                }

                int partLength = part.length();
                int visiblePartLength = Math.min(partLength, remaining);
                Component visiblePart = part.slice(visiblePartLength);
                result = result == null ? visiblePart : result.append(visiblePart);
                remaining -= visiblePartLength;
            }

            return result == null ? Component.empty() : result;
        }

        private static void appendParts(Component component, Style parentStyle, List<TextPart> parts) {
            Style style = parentStyle.merge(component.style(), Style.Merge.Strategy.ALWAYS);

            if (component instanceof net.kyori.adventure.text.TextComponent textComponent) {
                String content = textComponent.content();
                if (!content.isEmpty()) {
                    parts.add(new TextPart(content, style));
                }
            }

            for (Component child : component.children()) {
                appendParts(child, style, parts);
            }
        }
    }

    private record TextPart(String content, Style style, int length) {
        private TextPart(String content, Style style) {
            this(
                    Objects.requireNonNull(content, "content"),
                    Objects.requireNonNull(style, "style"),
                    content.codePointCount(0, content.length())
            );
        }

        private Component slice(int length) {
            if (length <= 0) {
                return Component.empty();
            }

            String visibleContent = length >= this.length
                    ? content
                    : content.substring(0, content.offsetByCodePoints(0, length));

            return Component.text()
                    .content(visibleContent)
                    .style(style)
                    .build();
        }
    }
}
