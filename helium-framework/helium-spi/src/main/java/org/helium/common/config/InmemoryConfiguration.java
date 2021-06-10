
package org.helium.common.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * In-memory configuration
 */
public class InmemoryConfiguration implements Configuration {

    // stores the configuration key-value pairs
    private Map<String, String> store = new LinkedHashMap<>();

    @Override
    public Object getInternalProperty(String key) {
        return store.get(key);
    }

    /**
     * Add one property into the store, the previous value will be replaced if the key exists
     */
    public void addProperty(String key, String value) {
        store.put(key, value);
    }

    /**
     * Add a set of properties into the store
     */
    public void addProperties(Map<String, String> properties) {
        if (properties != null) {
            this.store.putAll(properties);
        }
    }

    /**
     * set store
     */
    public void setProperties(Map<String, String> properties) {
        if (properties != null) {
            this.store = properties;
        }
    }

    // for unit test
    public void clear() {
        this.store.clear();
    }
}
