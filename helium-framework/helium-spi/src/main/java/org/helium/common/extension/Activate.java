
package org.helium.common.extension;

import org.helium.common.URL;

import java.lang.annotation.*;

/**
 * Activate. This annotation is useful for automatically activate certain extensions with the given criteria,
 * for examples: <code>@Activate</code> can be used to load certain <code>Filter</code> extension when there are
 * multiple implementations.
 * <ol>
 * <li>{@link Activate#group()} specifies group criteria. Framework SPI defines the valid group values.
 * <li>{@link Activate#value()} specifies parameter key in {@link URL} criteria.
 * </ol>
 * SPI provider can call {@link ExtensionLoader#getActivateExtension(URL, String, String)} to find out all activated
 * extensions with the given criteria.
 *
 * @see SPI
 * @see URL
 * @see ExtensionLoader
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Activate {
    /**
     * Activate the current extension when one of the groups matches. The group passed into
     * {@link ExtensionLoader#getActivateExtension(URL, String, String)} will be used for matching.
     *
     * @return group names to match
     * @see ExtensionLoader#getActivateExtension(URL, String, String)
     */
    String[] group() default {};

    /**
     * Activate the current extension when the specified keys appear in the URL's parameters.
     * <p>
     * For example, given <code>@Activate("cache, validation")</code>, the current extension will be return only when
     * there's either <code>cache</code> or <code>validation</code> key appeared in the URL's parameters.
     * </p>
     *
     * @return URL parameter keys
     * @see ExtensionLoader#getActivateExtension(URL, String)
     * @see ExtensionLoader#getActivateExtension(URL, String, String)
     */
    String[] value() default {};

    /**
     * Relative ordering info, optional
     * Deprecated since 2.7.0
     *
     * @return extension list which should be put before the current one
     */
    @Deprecated
    String[] before() default {};

    /**
     * Relative ordering info, optional
     * Deprecated since 2.7.0
     *
     * @return extension list which should be put after the current one
     */
    @Deprecated
    String[] after() default {};

    /**
     * Absolute ordering info, optional
     *
     * @return absolute ordering info
     */
    int order() default 0;
}