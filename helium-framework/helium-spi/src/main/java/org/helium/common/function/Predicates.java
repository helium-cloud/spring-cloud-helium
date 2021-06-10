
package org.helium.common.function;

import java.util.function.Predicate;

import static java.util.stream.Stream.of;

/**
 * The utilities class for Java {@link Predicate}
 *
 * @since 2.7.5
 */
public interface Predicates {

    /**
     * {@link Predicate} always return <code>true</code>
     *
     * @param <T> the type to test
     * @return <code>true</code>
     */
    static <T> Predicate<T> alwaysTrue() {
        return e -> true;
    }

    /**
     * {@link Predicate} always return <code>false</code>
     *
     * @param <T> the type to test
     * @return <code>false</code>
     */
    static <T> Predicate<T> alwaysFalse() {
        return e -> false;
    }

    /**
     * a composed predicate that represents a short-circuiting logical AND of {@link Predicate predicates}
     *
     * @param predicates {@link Predicate predicates}
     * @param <T>        the type to test
     * @return non-null
     */
    static <T> Predicate<T> and(Predicate<T>... predicates) {
        return of(predicates).reduce((a, b) -> a.and(b)).orElseGet(Predicates::alwaysTrue);
    }

    /**
     * a composed predicate that represents a short-circuiting logical OR of {@link Predicate predicates}
     *
     * @param predicates {@link Predicate predicates}
     * @param <T>        the detected type
     * @return non-null
     */
    static <T> Predicate<T> or(Predicate<T>... predicates) {
        return of(predicates).reduce((a, b) -> a.or(b)).orElse(e -> true);
    }

}
