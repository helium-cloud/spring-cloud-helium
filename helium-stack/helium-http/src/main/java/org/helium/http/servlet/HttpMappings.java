package org.helium.http.servlet;

import org.helium.framework.annotations.ServletMappingsClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Created by Coral on 7/8/15.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ServletMappingsClass(HttpServletMappings.class)
public @interface HttpMappings {
	String contextPath();
	String urlPattern();
	String[] moreUrlPatterns() default {};
}
