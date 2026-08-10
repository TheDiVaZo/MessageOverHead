package me.thedivazo.messageoverhead.animation;

import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.Author;
import me.thedivazo.messageoverhead.core.Message;
import me.thedivazo.messageoverhead.core.component.ComponentContainer;
import me.thedivazo.messageoverhead.core.component.ComponentRegistry;
import me.thedivazo.messageoverhead.core.component.DefaultComponentContainer;
import me.thedivazo.messageoverhead.core.component.TextComponent;
import me.thedivazo.messageoverhead.core.render.capability.CapabilityContainer;
import me.thedivazo.messageoverhead.core.render.capability.TextCapability;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnimationTypingTextComponentScopedTest {
    @Test
    void revealsActiveBubbleTextWithConfiguredDelays() {
        FakeBubble bubble = new FakeBubble(List.of(Component.text("ab"), Component.text("c")));
        AnimationTypingTextComponentScoped animation = new AnimationTypingTextComponentScoped(
                new AnimationTypingTextComponentScoped.Settings(1, 1, 2, true, Component.text("|"), 0)
        );

        animation.onAttached(bubble);

        assertEquals(List.of(Component.text("|")), bubble.lines());
        assertEquals(3, animation.totalCharacters());

        assertEquals(List.of(Component.text("|")), tick(animation, bubble));
        assertEquals(List.of(withCursor("a")), tick(animation, bubble));
        assertEquals(List.of(withCursor("ab")), tick(animation, bubble));
        assertEquals(List.of(withCursor("ab")), tick(animation, bubble));
        assertEquals(List.of(Component.text("ab"), Component.text("|")), tick(animation, bubble));
        assertEquals(List.of(Component.text("ab"), Component.text("c")), tick(animation, bubble));
        assertTrue(animation.isComplete());
    }

    @Test
    void preservesTextStylesWhenSlicingActiveBubbleText() {
        Component line = Component.text("red", NamedTextColor.RED)
                .append(Component.text("blue", NamedTextColor.BLUE));
        FakeBubble bubble = new FakeBubble(List.of(line));
        AnimationTypingTextComponentScoped animation = new AnimationTypingTextComponentScoped(
                new AnimationTypingTextComponentScoped.Settings(4)
        );

        animation.onAttached(bubble);
        tick(animation, bubble);
        tick(animation, bubble);
        tick(animation, bubble);
        List<Component> visibleLines = tick(animation, bubble);

        assertEquals(List.of(Component.text("r", NamedTextColor.RED)), visibleLines);
        assertEquals(1, animation.visibleCharacters());
        assertFalse(animation.isComplete());
    }

    @Test
    void restartFromActiveBubbleCapturesCurrentLiveText() {
        FakeBubble bubble = new FakeBubble(List.of(Component.text("old")));
        AnimationTypingTextComponentScoped animation = new AnimationTypingTextComponentScoped();

        animation.onAttached(bubble);
        tick(animation, bubble);

        bubble.setLines(List.of(Component.text("xy")));
        animation.restartFromActiveBubble();

        assertEquals(0, animation.visibleCharacters());
        assertEquals(2, animation.totalCharacters());
        assertEquals(List.of(), bubble.lines());

        assertEquals(List.of(Component.text("x")), tick(animation, bubble));
    }

    @Test
    void getOrAttachAnimatesTextComponentOfActiveBubble() {
        FakeBubble bubble = new FakeBubble(List.of(Component.text("live")));

        AnimationTypingTextComponentScoped animation = AnimationTypingTextComponentScoped.getOrAttach(
                bubble,
                new AnimationTypingTextComponentScoped.Settings(1)
        );

        assertNotNull(animation);
        assertEquals(List.of(), bubble.lines());

        bubble.tickComponents();

        assertEquals(List.of(Component.text("l")), bubble.lines());
    }

    @Test
    void rejectsInvalidSettings() {
        assertThrows(IllegalArgumentException.class, () ->
                new AnimationTypingTextComponentScoped.Settings(0, 0, 0, true, null, 0)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new AnimationTypingTextComponentScoped.Settings(1, -1, 0, true, null, 0)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new AnimationTypingTextComponentScoped.Settings(1, 0, -1, true, null, 0)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new AnimationTypingTextComponentScoped.Settings(1, 0, 0, true, null, -1)
        );
    }

    private static List<Component> tick(AnimationTypingTextComponentScoped animation, FakeBubble bubble) {
        TextComponent.TextState state = new TextComponent.TextState();
        animation.onTick(state);
        bubble.setLines(state.getLines());
        return state.getLines();
    }

    private static Component withCursor(String text) {
        return Component.text(text).append(Component.text("|"));
    }

    private static final class FakeBubble implements ActiveBubble, CapabilityContainer {
        private final UUID id = UUID.randomUUID();
        private final Message message;
        private final FakeTextCapability textCapability;
        private final DefaultComponentContainer components;

        private FakeBubble(List<Component> lines) {
            ComponentRegistry registry = new ComponentRegistry();
            registry.register(TextComponent.key());

            this.message = new Message(lines);
            this.textCapability = new FakeTextCapability(lines);
            this.components = new DefaultComponentContainer(registry, this);
        }

        private List<Component> lines() {
            return textCapability.getLines();
        }

        private void setLines(List<Component> lines) {
            textCapability.setLines(lines);
        }

        private void tickComponents() {
            components.tick();
        }

        @Override
        public Author author() {
            return null;
        }

        @Override
        public UUID id() {
            return id;
        }

        @Override
        public Message message() {
            return message;
        }

        @Override
        public long ageTicks() {
            return 0;
        }

        @Override
        public boolean isRemove() {
            return false;
        }

        @Override
        public void remove() {
        }

        @Override
        public ComponentContainer container() {
            return components;
        }

        @Override
        public CapabilityContainer capabilities() {
            return this;
        }

        @Override
        public <T> T capabilityOrNull(Class<T> type) {
            Objects.requireNonNull(type, "type");
            return type.isInstance(textCapability) ? type.cast(textCapability) : null;
        }
    }

    private static final class FakeTextCapability implements TextCapability {
        private final List<Component> lines = new ArrayList<>();

        private FakeTextCapability(List<Component> lines) {
            setLines(lines);
        }

        @Override
        public Component getLine(int index) {
            return lines.get(index);
        }

        @Override
        public List<Component> getLines() {
            return List.copyOf(lines);
        }

        @Override
        public void setLines(List<Component> components) {
            this.lines.clear();
            this.lines.addAll(components);
        }

        @Override
        public void insertLine(int index, Component component) {
            lines.add(index, component);
        }

        @Override
        public Component removeLine(int index) {
            return lines.remove(index);
        }

        @Override
        public void setLine(int index, Component component) {
            lines.set(index, component);
        }

        @Override
        public void addLine(Component component) {
            lines.add(component);
        }
    }
}
