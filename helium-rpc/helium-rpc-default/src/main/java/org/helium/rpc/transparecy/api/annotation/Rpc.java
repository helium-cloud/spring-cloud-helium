package org.helium.rpc.transparecy.api.annotation;

import java.lang.annotation.*;

/**
 * RPC映射注解，用来描述RPC映射信息
 * <p>
 * Created by Coral on 2015/5/18.
 */
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Rpc {

	/**
	 * 映射的RPC服务名
	 *
	 * @return RPC服务名
	 */
	String service() default "";

	/**
	 * 映射的RPC方法名
	 *
	 * @return RPC方法名
	 */
	String method() default "";
}
