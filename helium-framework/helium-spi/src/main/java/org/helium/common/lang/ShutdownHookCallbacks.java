
package org.helium.common.lang;

import org.helium.common.extension.ExtensionLoader;
import org.helium.common.function.ThrowableAction;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.sort;

/**
 * The compose {@link ShutdownHookCallback} class to manipulate one and more {@link ShutdownHookCallback} instances
 *
 * @since 2.7.5
 */
public class ShutdownHookCallbacks {

    public static final ShutdownHookCallbacks INSTANCE = new ShutdownHookCallbacks();

    private final List<ShutdownHookCallback> callbacks = new LinkedList<>();

    ShutdownHookCallbacks() {
        loadCallbacks();
    }

    public ShutdownHookCallbacks addCallback(ShutdownHookCallback callback) {
        synchronized (this) {
            this.callbacks.add(callback);
        }
        return this;
    }

    public Collection<ShutdownHookCallback> getCallbacks() {
        synchronized (this) {
            sort(this.callbacks);
            return this.callbacks;
        }
    }

    public void clear() {
        synchronized (this) {
            callbacks.clear();
        }
    }

    private void loadCallbacks() {
        ExtensionLoader<ShutdownHookCallback> loader =
                ExtensionLoader.getExtensionLoader(ShutdownHookCallback.class);
        loader.getSupportedExtensionInstances().forEach(this::addCallback);
    }

    public void callback() {
        getCallbacks().forEach(callback -> ThrowableAction.execute(callback::callback));
    }
}