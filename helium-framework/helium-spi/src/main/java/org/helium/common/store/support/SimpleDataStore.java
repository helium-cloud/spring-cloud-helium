

package org.helium.common.store.support;

import org.helium.common.store.DataStore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SimpleDataStore implements DataStore {

    // <component name or id, <data-name, data-value>>
    private ConcurrentMap<String, ConcurrentMap<String, Object>> data =
            new ConcurrentHashMap<String, ConcurrentMap<String, Object>>();

    @Override
    public Map<String, Object> get(String componentName) {
        ConcurrentMap<String, Object> value = data.get(componentName);
        if (value == null) {
            return new HashMap<String, Object>();
        }

        return new HashMap<String, Object>(value);
    }

    @Override
    public Object get(String componentName, String key) {
        if (!data.containsKey(componentName)) {
            return null;
        }
        return data.get(componentName).get(key);
    }

    @Override
    public void put(String componentName, String key, Object value) {
        Map<String, Object> componentData = data.computeIfAbsent(componentName, k -> new ConcurrentHashMap<>());
        componentData.put(key, value);
    }

    @Override
    public void remove(String componentName, String key) {
        if (!data.containsKey(componentName)) {
            return;
        }
        data.get(componentName).remove(key);
    }

}
