
package org.helium.common.config;


/**
 * FIXME: is this really necessary? PropertiesConfiguration should have already covered this:
 *
 * @See ConfigUtils#getProperty(String)
 * @see PropertiesConfiguration
 */
public class SystemConfiguration implements Configuration {

    @Override
    public Object getInternalProperty(String key) {
        return System.getProperty(key);
    }

}
