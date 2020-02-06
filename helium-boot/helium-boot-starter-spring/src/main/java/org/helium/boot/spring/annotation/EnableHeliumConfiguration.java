package org.helium.boot.spring.annotation;

import java.lang.annotation.*;

/**
 * Enable Helium for spring boot application
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableHeliumConfiguration {

}
