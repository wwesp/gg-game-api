package edu.missouriwestern.csmp.gg.base;

import java.util.stream.Stream;

@Deprecated // only Game should be an event producer, no need for the interface
public interface EventProducer extends EventListener {
    public void registerListener(EventListener listener);
    public void deregisterListener(EventListener listener);
    public Stream<EventListener> getListeners();
    public default void accept(Event event) {
        propagateEvent(event);
    }
    public default void propagateEvent(Event event) {
        getListeners().forEach(listener -> listener.accept(event));
    }
}
