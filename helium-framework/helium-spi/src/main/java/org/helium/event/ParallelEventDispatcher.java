
package org.helium.event;

import java.util.concurrent.ForkJoinPool;

/**
 * Parallel {@link EventDispatcher} implementation uses {@link ForkJoinPool#commonPool() JDK common thread pool}
 *
 * @see ForkJoinPool#commonPool()
 * @since 2.7.5
 */
public class ParallelEventDispatcher extends AbstractEventDispatcher {

    public ParallelEventDispatcher() {
        super(ForkJoinPool.commonPool());
    }
}
