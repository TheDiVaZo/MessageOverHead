package me.thedivazo.messageoverhead.common.controller;

import com.google.common.collect.Sets;
import me.thedivazo.messageoverhead.common.visualizer.Visualizer;
import me.thedivazo.messageoverhead.common.bubble.Bubble;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TheDiVaZo
 * created on 07.10.2024
 */
public class CopyOnWriteDisplayControllerImpl<B extends Bubble<?, ?>, S> implements DisplayController<B, S> {
    private final Visualizer<B, S> visualizer;
    private final Map<B, Set<S>> spectatorContainer = new ConcurrentHashMap<>();

    public CopyOnWriteDisplayControllerImpl(Visualizer<B, S> visualizer) {
        this.visualizer = visualizer;
    }

    @Override
    public @Nullable B reloadBubble(B bubble) {
        Set<S> spectators = spectatorContainer.get(bubble);
        if (spectators == null)
            return null;
        visualizer.removeVisualization(bubble, spectators);
        visualizer.visualize(bubble, spectators);
        return bubble;
    }

    @Override
    public @Nullable Set<S> showBubble(B bubble, Set<S> spectators) {
        Set<S> prevSpectators = spectatorContainer.put(bubble, Set.copyOf(spectators));
        if (prevSpectators!= null)
            visualizer.removeVisualization(bubble, prevSpectators);
        visualizer.visualize(bubble, spectators);
        return prevSpectators;
    }

    @Override
    public boolean addSpectatorAndShow(B bubble, Set<S> newSpectators) {
        if (newSpectators.isEmpty()) return false;
        Set<S> spectators = spectatorContainer.get(bubble);
        if (spectators == null) {
            showBubble(bubble, newSpectators);
            return true;
        }
        Set<S> differenceSpectator = Sets.difference(newSpectators, spectators);
        if (differenceSpectator.isEmpty()) return false;
        spectatorContainer.put(bubble, Sets.union(differenceSpectator, newSpectators).immutableCopy());
        visualizer.visualize(bubble, differenceSpectator);
        return true;
    }

    @Override
    public @Nullable Set<S> hideBubble(B bubble) {
        Set<S> spectators = spectatorContainer.remove(bubble);
        if (spectators!= null) visualizer.removeVisualization(bubble, spectators);
        return spectators;
    }

    @Override
    public boolean removeSpectatorAndHide(B bubble, Set<S> remSpectators) {
        if (remSpectators.isEmpty())
            return false;
        Set<S> spectators = spectatorContainer.get(bubble);
        if (spectators == null)
            return false;
        Set<S> intersectionSpectator = Sets.intersection(remSpectators, spectators);
        if (intersectionSpectator.isEmpty()) return false;
        spectatorContainer.put(bubble, Sets.difference(spectators, intersectionSpectator).immutableCopy());
        visualizer.removeVisualization(bubble, intersectionSpectator);
        return true;
    }

    @Override
    public void hideAll() {
        spectatorContainer.forEach(visualizer::removeVisualization);
        spectatorContainer.clear();
    }
}
