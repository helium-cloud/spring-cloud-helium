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
 * Rpc客户端的自动注解
 * <p>
 * Created by Coral
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RpcMethod {
	/**
	 * Rpc方法的名字
	 */
	String value() default "";
//
//	/** Rpc方法的请求类型, 在使用继承RpcServiceBase的实现方式时, 可增加安全性并提升效率 */
//	Class<?> argsType() default Void.class;
//
//	/** Rpc方法的应答类型, 在使用继承RpcServiceBase的实现方式时, 可增加安全性并提升效率 */
//	Class<?> resultsType() default Void.class;
}
