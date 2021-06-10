
package org.helium.common.status;

import org.helium.common.extension.SPI;

/**
 * StatusChecker
 */
@SPI
public interface StatusChecker {

    /**
     * check status
     *
     * @return status
     */
    Status check();

}