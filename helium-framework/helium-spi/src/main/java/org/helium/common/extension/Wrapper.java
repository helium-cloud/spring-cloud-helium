
package org.helium.common.extension;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The annotated class will only work as a wrapper when the condition matches.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Wrapper {

    /**
     * the extension names that need to be wrapped.
     */
    String[] matches() default {};

    /**
     * the extension names that need to be excluded.
     */
    String[] mismatches() default {};
}
