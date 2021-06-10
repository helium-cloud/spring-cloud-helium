
package org.helium.common.lang;

import org.helium.common.extension.SPI;

/**
 * helium ShutdownHook callback interface
 *
 * @since 2.7.5
 */
@SPI
public interface ShutdownHookCallback extends Prioritized {

    /**
     * Callback execution
     *
     * @throws Throwable if met with some errors
     */
    void callback() throws Throwable;
}
