package me.thedivazo.messageoverhead.core.event;

import com.google.common.collect.*;
import me.thedivazo.messageoverhead.annotation.MainThread;

import java.util.*;

@MainThread
public final class SimpleEventBus implements EventBus {

    private final SetMultimap<Class<?>, Handler<?>> handlers = LinkedHashMultimap.create();
    private final SetMultimap<Handler<?>, Class<?>> subscribedEvents = LinkedHashMultimap.create();

    private boolean locked = false;
    private final Queue<Runnable> queue = new ArrayDeque<>();

    @Override
    public <T> void subscribe(Class<T> eventType, Handler<? super T> consumer) {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(consumer, "consumer");

        executeOrQueue(() -> {
            handlers.put(eventType, consumer);
            subscribedEvents.put(consumer, eventType);
        });
    }

    @Override
    public void unsubscribe(Handler<?> consumer) {
        Objects.requireNonNull(consumer, "consumer");

        executeOrQueue(() -> {
            Set<Class<?>> eventTypes = subscribedEvents.removeAll(consumer);

            for(Class<?> eventType : eventTypes) {
                handlers.remove(eventType, consumer);
            }
        });
    }

    @Override
    public void unsubscribeAll(Class<?> eventType) {
        Objects.requireNonNull(eventType, "eventType");

        executeOrQueue(() -> {
            Set<Handler<?>> removedHandlers = handlers.removeAll(eventType);

            for (Handler<?> handler : removedHandlers) {
                subscribedEvents.remove(handler, eventType);
            }
        });
    }

    @Override
    public void post(Object event) {
        Objects.requireNonNull(event, "event");

        if (locked) {
            queue.offer(() -> post(event));
            return;
        }

        locked = true;

        try {
            Class<?> postedEventType = event.getClass();
            handlers.entries().forEach(entry -> {
                Class<?> registeredEventType = entry.getKey();
                if (!registeredEventType.isAssignableFrom(postedEventType)) {
                    return;
                }
                try {
                    invoke(entry.getValue(), registeredEventType, event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } finally {
            locked = false;
            flushQueue();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void invoke(
            Handler<?> rawHandler,
            Class<T> eventType,
            Object event
    ) {
        Handler<? super T> handler = (Handler<? super T>) rawHandler;
        T typedEvent = eventType.cast(event);

        handler.handle(typedEvent);
    }

    private void executeOrQueue(Runnable action) {
        if (locked) {
            queue.offer(action);
        } else {
            action.run();
        }
    }

    private void flushQueue() {
        while (!queue.isEmpty()) {
            try {
                queue.poll().run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
