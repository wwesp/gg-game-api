package edu.missouriwestern.csmp.gg.base;

import java.util.Collections;
import java.util.Map;

public class Event implements HasProperties {

    private final Map<String,String> properties;
    private final int id;

    public Event(int id, Map<String,String> properties) {
        this.id = id;
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

    public String toString() {
        return "{ \"id\": " + id +
                ", \"type\": " + getClass().getSimpleName() +
                ", \"properties\": " + this.serializeProperties() +
        "}";

    }
}
