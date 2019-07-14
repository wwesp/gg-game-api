package edu.missouriwestern.csmp.gg.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


// TODO: create annotation indicating required and optional properties

/** Indicates when something happens during the game that other game components may react to.
 * Also used to record important events in the processing of the game.
 */
public class Event implements HasProperties {

    private final Map<String,String> properties;
    private final int id;
    private final Game game;
    private final long eventTime;

    public Event(Game game) {
        this(game, game.getNextEventId(), new HashMap<>());
    }

    // TODO: eliminate this
    public Event(Game game, int id) {
        this(game, id, new HashMap<>());
    }

    public Event(Game game, Map<String,String> properties) {
        this(game, game.getNextEventId(), properties);
    }

    // TODO: get rid of id param
    public Event(Game game, int id, Map<String,String> properties) {
        this.id = id;
        this.game = game;
        this.eventTime = game.getGameTime();
        properties = new HashMap<>(properties); // add id to properties
        properties.put("id", ""+id);
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

    /** time elapsed since start of game when this event occurred */
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
