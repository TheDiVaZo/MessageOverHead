package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.annotation.MainThread;
import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.render.capability.TextCapability;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@MainThread
public final class TextComponent implements BubbleComponent {
    private static final ComponentKey<TextComponent> KEY = new ComponentKey<>(
            ComponentId.of("messageoverhead", "text"),
            TextComponent.class,
            new ComponentMetadata(
                    Set.of(TextCapability.class),
                    TypeComponent.BUBBLE
            )
    );

    private final ActiveBubble activeBubble;
    private final TextCapability textCapability;

    private TextComponent(ActiveBubble activeBubble, TextCapability textCapability) {
        this.activeBubble = Objects.requireNonNull(activeBubble, "activeBubble");
        this.textCapability = Objects.requireNonNull(textCapability, "textCapability");
    }

    public ActiveBubble activeBubble() {
        return activeBubble;
    }

    public List<Component> lines() {
        return textCapability.getLines();
    }

    public List<Component> getLines() {
        return lines();
    }

    public Component line(int index) {
        return textCapability.getLine(index);
    }

    public Component getLine(int index) {
        return line(index);
    }

    public int lineCount() {
        return lines().size();
    }

    public int getLineCount() {
        return lineCount();
    }

    public void setText(String text) {
        setText(Component.text(Objects.requireNonNull(text, "text")));
    }

    public void setText(Component text) {
        setLines(List.of(Objects.requireNonNull(text, "text")));
    }

    public void setLines(List<Component> lines) {
        textCapability.setLines(copyLines(lines));
    }

    public void setLine(int index, String line) {
        setLine(index, Component.text(Objects.requireNonNull(line, "line")));
    }

    public void setLine(int index, Component line) {
        textCapability.setLine(index, Objects.requireNonNull(line, "line"));
    }

    public void insertLine(int index, String line) {
        insertLine(index, Component.text(Objects.requireNonNull(line, "line")));
    }

    public void insertLine(int index, Component line) {
        textCapability.insertLine(index, Objects.requireNonNull(line, "line"));
    }

    public void addLine(String line) {
        addLine(Component.text(Objects.requireNonNull(line, "line")));
    }

    public void addLine(Component line) {
        textCapability.addLine(Objects.requireNonNull(line, "line"));
    }

    public Component removeLine(int index) {
        return textCapability.removeLine(index);
    }

    public void clear() {
        setLines(List.of());
    }

    public static ComponentKey<TextComponent> key() {
        return KEY;
    }

    public static Factory factory() {
        return Factory.INSTANCE;
    }

    public static @Nullable TextComponent attach(ActiveBubble activeBubble) {
        return activeBubble.container().attach(key(), factory());
    }

    public static @Nullable TextComponent detach(ActiveBubble activeBubble) {
        return activeBubble.container().detach(key());
    }

    public static @Nullable TextComponent get(ActiveBubble activeBubble) {
        return activeBubble.container().get(key());
    }

    public static @Nullable TextComponent getOrAttach(ActiveBubble activeBubble) {
        TextComponent component = get(activeBubble);
        return component != null ? component : attach(activeBubble);
    }

    public static boolean contains(ActiveBubble activeBubble) {
        return activeBubble.container().contains(key());
    }

    private static List<Component> copyLines(List<Component> lines) {
        Objects.requireNonNull(lines, "lines");
        List<Component> copy = new ArrayList<>(lines.size());
        for (int i = 0; i < lines.size(); i++) {
            copy.add(Objects.requireNonNull(lines.get(i), "lines[" + i + "]"));
        }
        return copy;
    }

    public static final class Factory implements BubbleComponentFactory<TextComponent> {
        private static final Factory INSTANCE = new Factory();

        private Factory() {
        }

        @Override
        public TextComponent create(ActiveBubble bubble) {
            return new TextComponent(
                    bubble,
                    bubble.capabilities().requireCapability(TextCapability.class)
            );
        }
    }
}
