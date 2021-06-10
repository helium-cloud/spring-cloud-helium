
package org.helium.common.extension;

/**
 * helium {@link LoadingStrategy}
 *
 * @since 2.7.7
 */
public class HeliumLoadingStrategy implements LoadingStrategy {

    @Override
    public String directory() {
        return "META-INF/helium/";
    }

    @Override
    public boolean overridden() {
        return true;
    }

    @Override
    public int getPriority() {
        return NORMAL_PRIORITY;
    }


}
