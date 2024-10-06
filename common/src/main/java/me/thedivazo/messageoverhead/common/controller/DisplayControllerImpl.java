package me.thedivazo.messageoverhead.common.controller;

import lombok.AllArgsConstructor;
import me.thedivazo.messageoverhead.common.bubble.Bubble;
import me.thedivazo.messageoverhead.common.spectator.SpectatorFinder;
import me.thedivazo.messageoverhead.util.Container;
import me.thedivazo.messageoverhead.util.IterableUtil;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collections;
import java.util.Set;

/**
 * @author TheDiVaZo
 * created on 06.10.2024
 */
@AllArgsConstructor
public class DisplayControllerImpl<B extends Bubble<?, K>, K, S> implements DisplayController<B, K> {

    private final SpectatorFinder<S, B> spectatorFinder;
    private final VisualController<B, S> visualController;
    private final Container<K, B> bubbleContainer;
    private final Container<B, Set<S>> spectatorContainer;

    protected boolean showBubble(B newBubble) {
        return false;
    }

    @Override
    public boolean showBubbleIfNotShowed(B newBubble) {
        return false;
    }

    @Override
    public void showBubbleAnyway(B newBubble) {

    }

    @Override
    public boolean updateSpectators(K creator) {
        return false;
    }

    @Override
    public @Nullable B replaceBubble(B newBubble) {
        return null;
    }

    @Override
    public boolean removeBubble(K creator) {
        return false;
    }
}
