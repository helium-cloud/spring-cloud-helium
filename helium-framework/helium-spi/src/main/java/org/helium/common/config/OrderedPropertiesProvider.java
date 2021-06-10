
package org.helium.common.config;


import org.helium.common.extension.SPI;

import java.util.Properties;

/**
 * 
 * The smaller value, the higher priority
 * 
 */
@SPI
public interface OrderedPropertiesProvider {
    /**
     * order
     *
     * @return
     */
    int priority();

    /**
     * load the properties
     *
     * @return
     */
    Properties initProperties();
}
