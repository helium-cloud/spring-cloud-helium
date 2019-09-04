package org.helium.http.webservice;

import org.helium.framework.tag.TagImplementationClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Coral on 8/25/15.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@TagImplementationClass(WebServiceImplementationTag.class)
public @interface WebServiceImplementation {
	/**
	 * path
	 * @return
	 */
	String value();
	// String stacks();
}
