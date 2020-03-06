package org.helium.rpc.stack;

import org.helium.framework.tag.TagImplementationClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Coral on 7/9/15.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@TagImplementationClass(LegacyRpcServiceTag.class)
public @interface LegacyRpcService {
	String serviceName();
}
