package me.thedivazo.messageoverhead.animation;

import me.thedivazo.messageoverhead.core.component.TextComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnimationTypingTextComponentScopedTest {
    @Test
    void revealsTextWithConfiguredDelays() {
        AnimationTypingTextComponentScoped animation = new AnimationTypingTextComponentScoped(
                List.of(Component.text("ab"), Component.text("c")),
                new AnimationTypingTextComponentScoped.Settings(1, 1, 2, true, Component.text("|"), 0)
        );
        TextComponent.TextState state = new TextComponent.TextState();

        animation.onTick(state);
        assertEquals(List.of(Component.text("|")), state.getLines());

        animation.onTick(state);
        assertEquals(List.of(withCursor("a")), state.getLines());

        animation.onTick(state);
        assertEquals(List.of(withCursor("ab")), state.getLines());

        animation.onTick(state);
        assertEquals(List.of(withCursor("ab")), state.getLines());

        animation.onTick(state);
        assertEquals(List.of(Component.text("ab"), Component.text("|")), state.getLines());

        animation.onTick(state);
        assertEquals(List.of(Component.text("ab"), Component.text("c")), state.getLines());
        assertTrue(animation.isComplete());
    }

    @Test
    void preservesTextStylesWhenSlicing() {
        Component line = Component.text("red", NamedTextColor.RED)
                .append(Component.text("blue", NamedTextColor.BLUE));
        AnimationTypingTextComponentScoped animation = new AnimationTypingTextComponentScoped(
                List.of(line),
                new AnimationTypingTextComponentScoped.Settings(4)
        );
        TextComponent.TextState state = new TextComponent.TextState();

        animation.onTick(state);
        animation.onTick(state);
        animation.onTick(state);
        animation.onTick(state);

        assertEquals(List.of(Component.text("r", NamedTextColor.RED)), state.getLines());
        assertEquals(1, animation.visibleCharacters());
        assertFalse(animation.isComplete());
    }

    @Test
    void resetsProgressWhenTargetTextChanges() {
        AnimationTypingTextComponentScoped animation = new AnimationTypingTextComponentScoped("old");
        TextComponent.TextState state = new TextComponent.TextState();

        animation.onTick(state);
        animation.setTargetText(
                List.of(Component.text("xy")),
                new AnimationTypingTextComponentScoped.Settings(1)
        );

        assertEquals(0, animation.visibleCharacters());
        assertEquals(2, animation.totalCharacters());
        assertFalse(animation.isComplete());

        animation.onTick(state);

        assertEquals(List.of(Component.text("x")), state.getLines());
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

    private static Component withCursor(String text) {
        return Component.text(text).append(Component.text("|"));
    }
}
