package org.helium.http.servlet.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Lei Gao on 7/28/15.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TemplateHtml {
	/***
	 *
	 * @return
	 */
	String value();

	/**
	 *
	 * @return
	 */
	boolean fromClassResource() default true;
}
