package me.thedivazo.messageoverhead.core.event;

public interface EventBus {
    <T> void subscribe(Class<T> eventType, Handler<? super T> consumer);
    void unsubscribe(Handler<?> consumer);
    void unsubscribeAll(Class<?> eventType);
    void post(Object event);
}
