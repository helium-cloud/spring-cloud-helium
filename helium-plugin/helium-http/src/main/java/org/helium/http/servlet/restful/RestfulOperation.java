package org.helium.http.servlet.restful;

/**
 * Created by Gao Lei on 1/12/17.
 */
public @interface RestfulOperation {
	RestfulMethod method();
	String path() default "";
}
