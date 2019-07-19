package edu.missouriwestern.csmp.gg.base;

import com.google.gson.GsonBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Event implements HasProperties {

    private final Map<String,String> properties;
    private final int id;
    private final Game game;
    private final long eventTime;

    public Event(Game game) {
        this(game, game.getNextEventId(), new HashMap<>());
    }

    public Event(Game game, int id) {
        this(game, id, new HashMap<>());
    }

    public Event(Game game, Map<String,String> properties) {
        this(game, game.getNextEventId(), properties);
    }

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
            var gsonBuilder = new GsonBuilder();
            var gson = gsonBuilder.create();
            var m = new HashMap<String,Object>();
            m.put("id", id);
            m.put("time", getEventTime());
            m.put("type", getClass().getSimpleName());
            m.put("properties", getProperties());
            return gson.toJson(m);
    }
}
