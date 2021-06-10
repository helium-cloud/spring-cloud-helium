
package org.helium.common.config.configcenter;

/**
 * Config change event type
 */
public enum ConfigChangeType {
    /**
     * A config is created.
     */
    ADDED,

    /**
     * A config is updated.
     */
    MODIFIED,

    /**
     * A config is deleted.
     */
    DELETED
}
