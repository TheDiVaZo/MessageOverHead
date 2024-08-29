package me.thedivazo.messageoverhead.common.controller;

import me.thedivazo.messageoverhead.common.bubble.Bubble;

public class AbstractDisplayController<B extends Bubble<?, K>, K> implements DisplayController<B, K>{
    @Override
    public void showBubble(B newBubble) {
        
    }

    @Override
    public void removeBubble(B bubble) {

    }

    @Override
    public void showCurrentBubble(K creator) {

    }

    @Override
    public void hideCurrentBubble(K creator) {

    }

    @Override
    public void removeCurrentBubble(K creator) {

    }

    @Override
    public B getCurrentBubble(K creator) {
        return null;
    }
}
