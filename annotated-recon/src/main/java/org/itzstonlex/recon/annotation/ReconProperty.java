package org.itzstonlex.recon.annotation;

import java.util.HashMap;
import java.util.Map;

public class ReconProperty {

    private final Map<String, String> properties = new HashMap<>();

    public boolean contains(String key) {
        return properties.containsKey(key);
    }

    public void set(String key, String value) {
        properties.put(key, value);
    }

    public String get(String key) {
        return properties.get(key);
    }

    public String getOrDefault(String key, String def) {
        return properties.getOrDefault(key, def);
    }

}
