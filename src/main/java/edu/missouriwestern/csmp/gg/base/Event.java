package edu.missouriwestern.csmp.gg.base;

import java.util.Collections;
import java.util.Map;

public class Event implements HasProperties {

    private final Map<String,String> properties;
    private final int id;
    private final Game game;
    private final long eventTime;

    public Event(Game game, int id, Map<String,String> properties) {
        this.id = id;
        this.game = game;
        this.eventTime = game.getGameTime();
        this.properties = Collections.unmodifiableMap(properties);
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public void setProperty(String key, String value) {
        throw new UnsupportedOperationException("Event properties are immutable");
    }

    public long getEventTime() {
        return eventTime;
    }

    public Game getGame() {
        return game;
    }

    public String toString() {
        return "{ \"id\": " + id +
                ", \"type\": " + getClass().getSimpleName() +
                ", \"properties\": " + this.serializeProperties() +
        "}";

    }
}
