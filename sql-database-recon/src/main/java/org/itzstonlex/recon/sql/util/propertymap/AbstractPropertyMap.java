package org.itzstonlex.recon.sql.util.propertymap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public abstract class AbstractPropertyMap<Value> implements PropertyMap<Value> {
    private final Map<String, Value> propertyMap = new ConcurrentHashMap<>();

    @Override
    public Value getProperty(String key) {
        return propertyMap.get(key);
    }

    @Override
    public Value getProperty(String key, Supplier<Value> def) {
        return propertyMap.containsKey(key) ? propertyMap.get(key) : def.get();
    }

    @Override
    public void setProperty(String key, Value value) {
        propertyMap.put(key, value);
    }

    @Override
    public boolean hasProperty(String key) {
        return propertyMap.containsKey(key);
    }

    @Override
    public Set<Value> values() {
        return new HashSet<>(propertyMap.values());
    }

    @Override
    public Set<String> keys() {
        return propertyMap.keySet();
    }

    @Override
    public Map<String, Value> map() {
        return propertyMap;
    }

    @Override
    public void reset() {
        propertyMap.clear();
    }

    @Override
    public void setProperties(PropertyMap<Value> propertyMap) {
        this.propertyMap.putAll(propertyMap.map());
    }

}
