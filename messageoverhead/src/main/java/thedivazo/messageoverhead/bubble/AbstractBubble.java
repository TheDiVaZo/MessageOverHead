package thedivazo.messageoverhead.bubble;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public abstract class AbstractBubble implements Bubble {
    protected String message;

    protected AbstractBubble(String message) {
        this.message = message;
    }
}
