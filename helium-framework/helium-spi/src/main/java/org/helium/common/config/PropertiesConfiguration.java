
package org.helium.common.config;

import org.helium.common.utils.ConfigUtils;
import org.helium.common.extension.ExtensionLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Configuration from system properties and helium.properties
 */
public class PropertiesConfiguration implements Configuration {

    public PropertiesConfiguration() {
        ExtensionLoader<OrderedPropertiesProvider> propertiesProviderExtensionLoader = ExtensionLoader.getExtensionLoader(OrderedPropertiesProvider.class);
        Set<String> propertiesProviderNames = propertiesProviderExtensionLoader.getSupportedExtensions();
        if (propertiesProviderNames == null || propertiesProviderNames.isEmpty()) {
            return;
        }
        List<OrderedPropertiesProvider> orderedPropertiesProviders = new ArrayList<>();
        for (String propertiesProviderName : propertiesProviderNames) {
            orderedPropertiesProviders.add(propertiesProviderExtensionLoader.getExtension(propertiesProviderName));
        }

        //order the propertiesProvider according the priority descending
        orderedPropertiesProviders.sort((OrderedPropertiesProvider a, OrderedPropertiesProvider b) -> {
            return b.priority() - a.priority();
        });

        //load the default properties
        Properties properties = ConfigUtils.getProperties();

        //override the properties.
        for (OrderedPropertiesProvider orderedPropertiesProvider :
                orderedPropertiesProviders) {
            properties.putAll(orderedPropertiesProvider.initProperties());
        }

        ConfigUtils.setProperties(properties);
    }

    @Override
    public Object getInternalProperty(String key) {
        return ConfigUtils.getProperty(key);
    }
}
