package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.annotation.MainThread;
import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.component.scope.BubbleScopeComponent;
import me.thedivazo.messageoverhead.core.component.scope.ComponentScoped;
import me.thedivazo.messageoverhead.core.component.scope.ScopedFactory;
import me.thedivazo.messageoverhead.core.render.capability.TextCapability;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@MainThread
public final class TextComponent implements BubbleScopeComponent<TextComponent.TextState> {
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
    private final Map<String, ComponentScoped<TextState>> components = new LinkedHashMap<>();

    private final TextState cachedTextState = new TextState();

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

    @Override
    public @Nullable ComponentScoped<TextState> attach(String id, ScopedFactory<TextState> scopedFactory) {
        Objects.requireNonNull(scopedFactory, "scopedFactory");
        String scopedId = requireScopedId(id);
        try {
            return addScoped(scopedId, createScoped(scopedId, scopedFactory));
        } catch (Exception | Error exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public @Nullable ComponentScoped<TextState> detach(String id) {
        ComponentScoped<TextState> detached = components.remove(requireScopedId(id));
        if (detached != null) {
            detached.onDetached();
        }
        return detached;
    }

    @Override
    public @Nullable ComponentScoped<TextState> get(String id) {
        return components.get(requireScopedId(id));
    }

    @Override
    public boolean contains(String id) {
        return components.containsKey(requireScopedId(id));
    }

    @Override
    public void onTick() {
        if (components.isEmpty()) {
            return;
        }

        cachedTextState.reset(textCapability.getLines());
        for (ComponentScoped<TextState> component : List.copyOf(components.values())) {
            component.onTick(cachedTextState);
        }

        if (cachedTextState.changed()) {
            textCapability.setLines(cachedTextState.lines());
        }
    }

    @Override
    public void onDetached() {
        detachAllScoped();
    }

    private ComponentScoped<TextState> addScoped(String id, ComponentScoped<TextState> componentScoped) {
        String scopedId = requireScopedId(id);
        Objects.requireNonNull(componentScoped, "componentScoped");
        ComponentScoped<TextState> previous = components.get(scopedId);
        if (previous == componentScoped) {
            return componentScoped;
        }
        componentScoped.onAttached(activeBubble);
        components.put(scopedId, componentScoped);
        if (previous != null) {
            previous.onDetached();
        }
        return componentScoped;
    }

    private ComponentScoped<TextState> createScoped(String id, ScopedFactory<TextState> scopedFactory) throws Exception {
        ComponentScoped<TextState> componentScoped = scopedFactory.create(activeBubble);
        if (componentScoped == null) {
            throw new IllegalStateException("Scoped factory returned null for " + id);
        }
        return componentScoped;
    }

    private void detachAllScoped() {
        for (ComponentScoped<TextState> component : components.values()) {
            try {
                component.onDetached();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        components.clear();
    }

    private static String requireScopedId(String id) {
        return Objects.requireNonNull(id, "id");
    }

    public static ComponentKey<TextComponent> key() {
        return KEY;
    }

    public static Factory factory() {
        return Factory.EMPTY;
    }

    public static Factory factory(Map<String, ScopedFactory<TextState>> scopedFactories) {
        return new Factory(scopedFactories);
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

    public static final class TextState {
        private List<Component> lines = List.of();
        private boolean changed;

        public List<Component> lines() {
            return lines;
        }

        public List<Component> getLines() {
            return lines();
        }

        public void setText(String text) {
            setText(Component.text(Objects.requireNonNull(text, "text")));
        }

        public void setText(Component text) {
            setLines(List.of(Objects.requireNonNull(text, "text")));
        }

        public void setLines(List<Component> lines) {
            this.lines = List.copyOf(copyLines(lines));
            changed = true;
        }

        public void clear() {
            setLines(List.of());
        }

        private boolean changed() {
            return changed;
        }

        private void reset(List<Component> lines) {
            this.lines = List.copyOf(copyLines(lines));
            changed = false;
        }
    }

    public static final class Factory implements BubbleComponentFactory<TextComponent> {
        private static final Factory EMPTY = new Factory(Map.of());

        private final Map<String, ScopedFactory<TextState>> scopedFactories;

        private Factory(Map<String, ScopedFactory<TextState>> scopedFactories) {
            Objects.requireNonNull(scopedFactories, "scopedFactories");
            Map<String, ScopedFactory<TextState>> scopedFactoriesCopy = new LinkedHashMap<>();
            for (Map.Entry<String, ScopedFactory<TextState>> entry : scopedFactories.entrySet()) {
                scopedFactoriesCopy.put(
                        requireScopedId(entry.getKey()),
                        Objects.requireNonNull(entry.getValue(), "scopedFactory")
                );
            }
            this.scopedFactories = Collections.unmodifiableMap(scopedFactoriesCopy);
        }

        @Override
        public TextComponent create(ActiveBubble bubble) throws Exception {
            TextComponent component = new TextComponent(
                    bubble,
                    bubble.capabilities().requireCapability(TextCapability.class)
            );
            for (Map.Entry<String, ScopedFactory<TextState>> entry : scopedFactories.entrySet()) {
                component.addScoped(entry.getKey(), component.createScoped(entry.getKey(), entry.getValue()));
            }
            return component;
        }
    }
}
