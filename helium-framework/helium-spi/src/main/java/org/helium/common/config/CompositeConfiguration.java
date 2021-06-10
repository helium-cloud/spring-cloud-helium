
package org.helium.common.config;

import org.helium.common.utils.SPIStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This is an abstraction specially customized for the sequence helium retrieves properties.
 */
public class CompositeConfiguration implements Configuration {
    private Logger logger = LoggerFactory.getLogger(CompositeConfiguration.class);

    private String id;
    private String prefix;

    /**
     * List holding all the configuration
     */
    private List<Configuration> configList = new LinkedList<Configuration>();

    //FIXME, consider change configList to SortedMap to replace this boolean status.
    private boolean dynamicIncluded;

    public CompositeConfiguration() {
        this(null, null);
    }

    public CompositeConfiguration(String prefix, String id) {
        if (SPIStringUtils.isNotEmpty(prefix) && !prefix.endsWith(".")) {
            this.prefix = prefix + ".";
        } else {
            this.prefix = prefix;
        }
        this.id = id;
    }

    public CompositeConfiguration(Configuration... configurations) {
        this();
        if (configurations != null && configurations.length > 0) {
            Arrays.stream(configurations).filter(config -> !configList.contains(config)).forEach(configList::add);
        }
    }

    public void setDynamicIncluded(boolean dynamicIncluded) {
        this.dynamicIncluded = dynamicIncluded;
    }

    //FIXME, consider change configList to SortedMap to replace this boolean status.
    public boolean isDynamicIncluded() {
        return dynamicIncluded;
    }

    public void addConfiguration(Configuration configuration) {
        if (configList.contains(configuration)) {
            return;
        }
        this.configList.add(configuration);
    }

    public void addConfigurationFirst(Configuration configuration) {
        this.addConfiguration(0, configuration);
    }

    public void addConfiguration(int pos, Configuration configuration) {
        this.configList.add(pos, configuration);
    }

    @Override
    public Object getInternalProperty(String key) {
        Configuration firstMatchingConfiguration = null;
        for (Configuration config : configList) {
            try {
                if (config.containsKey(key)) {
                    firstMatchingConfiguration = config;
                    break;
                }
            } catch (Exception e) {
                logger.error("Error when trying to get value for key " + key + " from " + config + ", will continue to try the next one.");
            }
        }
        if (firstMatchingConfiguration != null) {
            return firstMatchingConfiguration.getProperty(key);
        } else {
            return null;
        }
    }

    @Override
    public boolean containsKey(String key) {
        return configList.stream().anyMatch(c -> c.containsKey(key));
    }

    @Override
    public Object getProperty(String key, Object defaultValue) {
        Object value = null;
        if (SPIStringUtils.isNotEmpty(prefix)) {
            if (SPIStringUtils.isNotEmpty(id)) {
                value = getInternalProperty(prefix + id + "." + key);
            }
            if (value == null) {
                value = getInternalProperty(prefix + key);
            }
        } else {
            value = getInternalProperty(key);
        }
        return value != null ? value : defaultValue;
    }
}