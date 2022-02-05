package org.itzstonlex.recon.sql.util.propertymap;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public interface PropertyMap<Value> {

    Value getProperty(String key);

    Value getProperty(String key, Supplier<Value> def);

    void setProperty(String key, Value value);

    boolean hasProperty(String key);

    Set<Value> values();

    Set<String> keys();

    Map<String, Value> map();

    void reset();

    void setProperties(PropertyMap<Value> propertyMap);
}
