package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.Author;
import me.thedivazo.messageoverhead.core.Message;
import me.thedivazo.messageoverhead.core.render.capability.CapabilityContainer;
import me.thedivazo.messageoverhead.core.render.capability.TextCapability;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class TextComponentTest {
    @Test
    void delegatesLiveLineChangesToTextCapability() {
        ComponentRegistry registry = new ComponentRegistry();
        registry.register(TextComponent.key());
        FakeBubble bubble = new FakeBubble(registry, List.of(Component.text("one"), Component.text("two")));

        TextComponent textComponent = TextComponent.attach(bubble);

        assertNotNull(textComponent);
        assertSame(textComponent, TextComponent.get(bubble));

        textComponent.setText("updated");
        textComponent.addLine(Component.text("tail"));
        textComponent.insertLine(1, "middle");
        textComponent.setLine(2, "changed");
        Component removed = textComponent.removeLine(0);

        assertEquals(Component.text("updated"), removed);
        assertEquals(List.of(
                Component.text("middle"),
                Component.text("changed")
        ), textComponent.getLines());

        textComponent.clear();

        assertEquals(List.of(), bubble.textCapability.lines);
    }

    private static final class FakeBubble implements ActiveBubble, CapabilityContainer {
        private final UUID id = UUID.randomUUID();
        private final Message message;
        private final FakeTextCapability textCapability;
        private final DefaultComponentContainer components;

        private FakeBubble(ComponentRegistry registry, List<Component> lines) {
            this.message = new Message(lines);
            this.textCapability = new FakeTextCapability(lines);
            this.components = new DefaultComponentContainer(registry, this);
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
