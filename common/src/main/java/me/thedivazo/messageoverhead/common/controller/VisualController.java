package me.thedivazo.messageoverhead.common.controller;

import me.thedivazo.messageoverhead.common.bubble.Bubble;

public interface VisualController<B extends Bubble<?, ?>> {
    void visualize(B bubble);
    void removeVisualization(B bubble);
}
