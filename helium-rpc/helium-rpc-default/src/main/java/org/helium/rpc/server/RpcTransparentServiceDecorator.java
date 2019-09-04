/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2011-1-5
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.rpc.server;

import org.helium.rpc.channel.RpcReturnCode;
import org.helium.rpc.channel.RpcServerMethodHandler;
import org.helium.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 实现透明rpc服务的封装
 *
 * @author Coral
 * Created by Coral@feinno.com
 */
class RpcTransparentServiceDecorator extends RpcServiceBase {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcTransparentServiceDecorator.class);

	public static RpcTransparentServiceDecorator create(Object service) {
		Class<?> intfClazz = null;
		String serviceName = null;
		for (Class<?> i : service.getClass().getInterfaces()) {
			RpcService annotation = i.getAnnotation(RpcService.class);
			if (annotation != null) {
				if (intfClazz == null) {
					intfClazz = i;
					if (annotation.value() != "") {
						serviceName = annotation.value();
					} else {
						serviceName = i.getClass().getName();
					}
				} else {
					throw new IllegalArgumentException("service can only implements one interface with @RpcServer");
				}
			}
		}

		if (intfClazz == null) {
			throw new IllegalArgumentException("service must implements an interface with @RpcServer");
		}

		return new RpcTransparentServiceDecorator(service, serviceName, intfClazz);
	}

	private RpcTransparentServiceDecorator(Object service, String name, Class<?> intfClazz) {
		super(name, true);

		// 先将标记为@RpcMethod的方法加入: TODO: 有些混乱，但是
		RpcServiceBase.addAnnotatedMethods(this, service);

		// 将继承自interface的@RpcMethod加入
		for (Method method : intfClazz.getMethods()) {
			RpcMethod anno = method.getAnnotation(RpcMethod.class);
			if (anno == null) {
				continue;
			}

			// 找到方法名
			String methodName = anno.value().equals("") ? method.getName() : anno.value();

			// 
			RpcServerMethodHandler mh = createMethodHanlder(service, method);
			this.addMethodHandler(methodName, mh);
		}
	}

	private static RpcServerMethodHandler createMethodHanlder(final Object target, final Method method) {
		Class<?>[] params = method.getParameterTypes();
//		if (params.length != 1) {
//			throw new IllegalArgumentException("method not match:" + method.getName());
//		}

		final Class<?> argsType = params.length > 0 ? params[0] : null;
		final Class<?> resultsType = method.getReturnType();

		RpcServerMethodHandler mh = new RpcServerMethodHandler("", "") {
			@Override
			public void run(RpcServerContext ctx) {
				try {
					Object results = null;
					if (argsType != null) {
						Object args = ctx.getArgs(argsType);
						results = method.invoke(target, args);
					} else {
						results = method.invoke(target);
					}
					ctx.end(results);
				} catch (Exception ex) {
					//LOGGER.error("invoke failed, {}", ex);
					LOGGER.error(target.getClass() + " " + method.getName() + " " + ctx.getArgs(argsType) + " invoke failed, {}", ex);
					ctx.end(RpcReturnCode.SERVER_ERROR, ex);
				}
			}
		};

		if (argsType != null) {
			mh.setArgsCodec(Serializer.getCodec(argsType));
		}

		if (!void.class.equals(resultsType)) {
			mh.setResultsCodec(Serializer.getCodec(resultsType));
		}

		return mh;
	}
}
