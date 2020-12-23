package org.helium.framework.spring.annotation;

import org.helium.framework.spring.autoconfigure.LicenseAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Enable Helium for spring boot application
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableLicenseConfiguration {

}
