
package org.helium.event;

/**
 * Direct {@link EventDispatcher} implementation uses current thread execution model
 *
 * @see EventDispatcher
 * @since 2.7.5
 */
public final class DirectEventDispatcher extends AbstractEventDispatcher {

    public DirectEventDispatcher() {
        super(DIRECT_EXECUTOR);
    }
}
