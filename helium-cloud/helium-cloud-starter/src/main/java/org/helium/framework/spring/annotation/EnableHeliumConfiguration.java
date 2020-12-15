package org.helium.framework.spring.annotation;

import org.helium.framework.spring.autoconfigure.HeliumAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Enable Helium for spring boot application
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(HeliumAutoConfiguration.class)
@Documented
public @interface EnableHeliumConfiguration {

}
