
package org.helium.common.config.configcenter;

import java.util.EventListener;

/**
 * Config listener, will get notified when the config it listens on changes.
 */
public interface ConfigurationListener extends EventListener {

    /**
     * Listener call back method. Listener gets notified by this method once there's any change happens on the config
     * the listener listens on.
     *
     * @param event config change event
     */
    void process(ConfigChangedEvent event);
}
