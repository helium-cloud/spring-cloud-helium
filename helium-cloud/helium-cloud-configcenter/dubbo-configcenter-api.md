###
```
ConfigurationListener

public interface ConfigurationListener {

    /**
     * Listener call back method. Listener gets notified by this method once there's any change happens on the config
     * the listener listens on.
     *
     * @param event config change event
     */
    void process(ConfigChangeEvent event);
}


DynamicConfiguration

public interface DynamicConfiguration extends Configuration {
    String DEFAULT_GROUP = "dubbo";

    /**
     * {@link #addListener(String, String, ConfigurationListener)}
     *
     * @param key      the key to represent a configuration
     * @param listener configuration listener
     */
    default void addListener(String key, ConfigurationListener listener) {
        addListener(key, DEFAULT_GROUP, listener);
    }


    /*
     * {@link #removeListener(String, String, ConfigurationListener)}
     *
     * @param key      the key to represent a configuration
     * @param listener configuration listener
     */
    default void removeListener(String key, ConfigurationListener listener) {
        removeListener(key, DEFAULT_GROUP, listener);
    }

    /**
     * Register a configuration listener for a specified key
     * The listener only works for service governance purpose, so the target group would always be the value user
     * specifies at startup or 'dubbo' by default. This method will only register listener, which means it will not
     * trigger a notification that contains the current value.
     *
     * @param key      the key to represent a configuration
     * @param group    the group where the key belongs to
     * @param listener configuration listener
     */
    void addListener(String key, String group, ConfigurationListener listener);

    /**
     * Stops one listener from listening to value changes in the specified key.
     *
     * @param key      the key to represent a configuration
     * @param group    the group where the key belongs to
     * @param listener configuration listener
     */
    void removeListener(String key, String group, ConfigurationListener listener);

    /**
     * Get the configuration mapped to the given key
     *
     * @param key the key to represent a configuration
     * @return target configuration mapped to the given key
     */
    default String getConfig(String key) {
        return getConfig(key, null, 0L);
    }

    /**
     * Get the configuration mapped to the given key and the given group
     *
     * @param key   the key to represent a configuration
     * @param group the group where the key belongs to
     * @return target configuration mapped to the given key and the given group
     */
    default String getConfig(String key, String group) {
        return getConfig(key, group, 0L);
    }

    /**
     * Get the configuration mapped to the given key and the given group. If the
     * configuration fails to fetch after timeout exceeds, IllegalStateException will be thrown.
     *
     * @param key     the key to represent a configuration
     * @param group   the group where the key belongs to
     * @param timeout timeout value for fetching the target config
     * @return target configuration mapped to the given key and the given group, IllegalStateException will be thrown
     * if timeout exceeds.
     */
    String getConfig(String key, String group, long timeout) throws IllegalStateException;

    /**
     * Find DynamicConfiguration instance
     *
     * @return DynamicConfiguration instance
     */
    static DynamicConfiguration getDynamicConfiguration() {
        Optional<Configuration> optional = Environment.getInstance().getDynamicConfiguration();
        return (DynamicConfiguration) optional.orElseGet(() -> getExtensionLoader(DynamicConfigurationFactory.class)
                .getDefaultExtension()
                .getDynamicConfiguration(null));
    }
}

```