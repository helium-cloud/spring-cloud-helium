
package org.helium.event;

import org.helium.common.extension.ExtensionLoader;
import org.helium.common.extension.SPI;

import java.util.concurrent.Executor;

/**
 * {@link Event helium Event} Dispatcher
 *
 * @see Event
 * @see EventListener
 * @see DirectEventDispatcher
 * @since 2.7.5
 */
@SPI("direct")
public interface EventDispatcher extends Listenable<EventListener<?>> {

    /**
     * Direct {@link Executor} uses sequential execution model
     */
    Executor DIRECT_EXECUTOR = Runnable::run;

    /**
     * Dispatch a helium event to the registered {@link EventListener helium event listeners}
     *
     * @param event a {@link Event helium event}
     */
    void dispatch(Event event);

    /**
     * The {@link Executor} to dispatch a {@link Event helium event}
     *
     * @return default implementation directly invoke {@link Runnable#run()} method, rather than multiple-threaded
     * {@link Executor}. If the return value is <code>null</code>, the behavior is same as default.
     * @see #DIRECT_EXECUTOR
     */
    default Executor getExecutor() {
        return DIRECT_EXECUTOR;
    }

    /**
     * The default extension of {@link EventDispatcher} is loaded by {@link ExtensionLoader}
     *
     * @return the default extension of {@link EventDispatcher}
     */
    static EventDispatcher getDefaultExtension() {
        return ExtensionLoader.getExtensionLoader(EventDispatcher.class).getDefaultExtension();
    }
}
