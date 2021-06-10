
package org.helium.common.extension;

/**
 * Services {@link LoadingStrategy}
 *
 * @since 2.7.7
 */
public class ServicesLoadingStrategy implements LoadingStrategy {

    @Override
    public String directory() {
        return "META-INF/services/";
    }

    @Override
    public boolean overridden() {
        return true;
    }

    @Override
    public int getPriority() {
        return MIN_PRIORITY;
    }

}
