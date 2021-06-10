
package org.helium.common.config.configcenter;

import org.helium.common.URL;
import org.helium.common.constants.CommonConstants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract {@link DynamicConfigurationFactory} implementation with cache ability
 *
 * @see DynamicConfigurationFactory
 * @since 2.7.5
 */
public abstract class AbstractDynamicConfigurationFactory implements DynamicConfigurationFactory {

    private volatile Map<String, DynamicConfiguration> dynamicConfigurations = new ConcurrentHashMap<>();

    @Override
    public final DynamicConfiguration getDynamicConfiguration(URL url) {
        String key = url == null ? CommonConstants.DEFAULT_KEY : url.toServiceString();
        return dynamicConfigurations.computeIfAbsent(key, k -> createDynamicConfiguration(url));
    }

    protected abstract DynamicConfiguration createDynamicConfiguration(URL url);
}
