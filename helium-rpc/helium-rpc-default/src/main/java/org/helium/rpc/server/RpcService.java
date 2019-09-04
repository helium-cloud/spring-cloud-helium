/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2010-12-16
 *
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package org.helium.rpc.server;

import java.lang.annotation.*;

/**
 * Rpc服务的注解，用于标明透明的Rpc接口
 *
 * <pre>
 * <code>
 *    @RpcService("Hello")
 * 	public interface HelloService {
 * 		@RpcMethod("Add")
 * 		AddResults add(AddArgs args);
 * 	}
 * </code>
 * </pre>
 * <p>
 * Created by Coral
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcService {
	/**
	 * Rpc服务名
	 */
	String value() default "";

	/**
	 * 是否允许使用RpcServerContext.getCurrent()方法, 打开会降低性能, 建议使用直接继承RpcServiceBase的方式实现
	 */
	boolean threadContext() default false;
}
