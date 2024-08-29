package me.thedivazo.messageoverhead.common.controller;

import me.thedivazo.messageoverhead.common.bubble.Bubble;

import java.util.Set;

public interface SpectatorController<S, B extends Bubble<?, ?>> {
    Set<S> getSpectators(B bubble);
}
