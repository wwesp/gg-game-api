package edu.missouriwestern.csmp.gg.base;

import java.util.Map;

public interface HasProperties {
    public Map<String,String> getProperties();
    public void setProperty(String key, String value);

    public default String getProperty(String key) {
        var m = getProperties();
        if(!m.containsKey(key))
            throw new IllegalArgumentException("no such property " +
                key + " in " +this);
        return m.get(key);
    }

    public default String serializeProperties() {
        return "{" + getProperties().entrySet().stream()
                .map(e -> "\"" + e.getKey() + "\": " + e.getValue())
                .reduce((s1, s2) -> s1 + ","+ s2).orElse("") + "}";
    }
}
