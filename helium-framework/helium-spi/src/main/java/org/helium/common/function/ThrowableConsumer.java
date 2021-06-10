
package org.helium.common.function;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * {@link Consumer} with {@link Throwable}
 *
 * @param <T> the source type
 * @see Function
 * @see Throwable
 * @since 2.7.5
 */
@FunctionalInterface
public interface ThrowableConsumer<T> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @throws Throwable if met with any error
     */
    void accept(T t) throws Throwable;

    /**
     * Executes {@link ThrowableConsumer}
     *
     * @param t the function argument
     * @throws RuntimeException wrappers {@link Throwable}
     */
    default void execute(T t) throws RuntimeException {
        try {
            accept(t);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Executes {@link ThrowableConsumer}
     *
     * @param t        the function argument
     * @param consumer {@link ThrowableConsumer}
     * @param <T>      the source type
     * @return the result after execution
     */
    static <T> void execute(T t, ThrowableConsumer<T> consumer) {
        consumer.execute(t);
    }
}
