package me.thedivazo.messageoverhead.core.event;

@FunctionalInterface
public interface Handler<T> {
    void handle(T event);
}
