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

    protected B showBubble(B newBubble, boolean isGenerateNewSpectators) {
        B prevBubble = bubbleContainer.get(newBubble.creator());
        Set<S> prevSpectators = null;
        if (prevBubble != null) {
            prevSpectators = spectatorContainer.remove(prevBubble);
            if (prevSpectators != null) {
                visualController.removeVisualization(prevBubble, prevSpectators);
            }
        }
        Set<S> spectators = prevSpectators;
        if (spectators == null || isGenerateNewSpectators) spectators = spectatorFinder.getSpectators(newBubble);
        bubbleContainer.set(newBubble.creator(), newBubble);
        spectatorContainer.set(newBubble, spectators);
        visualController.visualize(newBubble, spectators);
        return prevBubble;
    }

    @Override
    public boolean showBubbleIfNotShowed(B newBubble) {
        if (bubbleContainer.has(newBubble.creator())) return false;
        showBubbleAnyway(newBubble);
        return true;
    }

    @Override
    public void showBubbleAnyway(B newBubble) {
        showBubble(newBubble, true);
    }

    @Override
    public boolean updateSpectators(K creator) {
        B bubble = bubbleContainer.get(creator);
        if (bubble == null) return false;
        showBubble(bubble, true);
        return true;
    }

    @Override
    public @Nullable B replaceBubble(B newBubble) {
        return showBubble(newBubble, false);
    }

    @Override
    public B removeBubble(K creator) {
        B bubble = bubbleContainer.remove(creator);
        if (bubble == null) return null;
        Set<S> spectators = spectatorContainer.remove(bubble);
        if (spectators!= null) visualController.removeVisualization(bubble, spectators);
        return bubble;
    }
}
