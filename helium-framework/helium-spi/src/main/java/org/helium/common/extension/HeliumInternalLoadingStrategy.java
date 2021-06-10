
package org.helium.common.extension;

/**
 * helium internal {@link LoadingStrategy}
 *
 * @since 2.7.7
 */
public class HeliumInternalLoadingStrategy implements LoadingStrategy {

    @Override
    public String directory() {
        return "META-INF/helium/internal/";
    }

    @Override
    public int getPriority() {
        return MAX_PRIORITY;
    }
}
