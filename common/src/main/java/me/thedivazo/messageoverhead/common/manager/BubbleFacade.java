package me.thedivazo.messageoverhead.common.manager;

import me.thedivazo.messageoverhead.common.bubble.Bubble;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Set;

/**
 * @author TheDiVaZo
 * created on 07.10.2024
 *
 * Интерфейс, предоставляющий методы управления всплывающими сообщениями
 */
public interface BubbleFacade<B extends Bubble<?, K>, K, S> {
    @Nullable B showBubble(B bubble, Set<S> spectators);
}
