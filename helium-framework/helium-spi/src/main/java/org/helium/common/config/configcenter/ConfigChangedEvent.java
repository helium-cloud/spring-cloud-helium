
package org.helium.common.config.configcenter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An event raised when the config changed, immutable.
 *
 * @see ConfigChangeType
 */
public class ConfigChangedEvent {

    private final String m_namespace;
    private final Map<String, ConfigChange> m_changes;

    public ConfigChangedEvent(String key, String group, String content) {
        this(key, group, content, ConfigChangeType.MODIFIED);
    }

    public ConfigChangedEvent(String key, String group, String content, ConfigChangeType changeType) {
        m_changes = new HashMap<>();
        ConfigChange configChange = new ConfigChange(group, key, "", content,changeType);
        m_changes.put(key, configChange);
        m_namespace = group;
    }

    /**
     * Constructor.
     *
     * @param namespace the namespace of this change
     * @param changes   the actual changes
     */
    public ConfigChangedEvent(String namespace,
                              Map<String, ConfigChange> changes) {
        m_namespace = namespace;
        m_changes = changes;
    }

    /**
     * Get the keys changed.
     *
     * @return the list of the keys
     */
    public Set<String> changedKeys() {
        return m_changes.keySet();
    }

    /**
     * Get a specific change instance for the key specified.
     *
     * @param key the changed key
     * @return the change instance
     */
    public ConfigChange getChange(String key) {
        return m_changes.get(key);
    }

    /**
     * Check whether the specified key is changed
     *
     * @param key the key
     * @return true if the key is changed, false otherwise.
     */
    public boolean isChanged(String key) {
        return m_changes.containsKey(key);
    }

    /**
     * Get the namespace of this change event.
     *
     * @return the namespace
     */
    public String getNamespace() {
        return m_namespace;
    }
}
