package thedivazo.messageoverhead.bubble;

import lombok.EqualsAndHashCode;
import org.bukkit.Location;
import thedivazo.messageoverhead.utils.text.DecoratedString;

@EqualsAndHashCode
public abstract class AbstractBubble implements Bubble {
    protected String message;

    protected AbstractBubble(String message) {
        this.message = message;
    }
}
