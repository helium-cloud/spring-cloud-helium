/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2011-1-5
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.rpc.stack;


import org.helium.rpc.channel.RpcReturnCode;
import org.helium.rpc.channel.RpcServerMethodHandler;
import org.helium.rpc.server.RpcMethod;
import org.helium.rpc.server.RpcServerContext;
import org.helium.rpc.server.RpcService;
import org.helium.rpc.server.RpcServiceBase;
import org.helium.serialization.Serializer;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;


/**
 * 从intf
 */
public class LegacyRpcDecorator extends RpcServiceBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(LegacyRpcDecorator.class);

    /**
     * 从intfClazz中反射出所有方法，
     * 1. 如果方法参数不为1个，报错IllegalArgumentException("legacyRpcService method only support 1 args");
     * 2. 如果方法返回值为RpcFuture<E>, 则透明封装为异步接口
     * 3. 其他非RpcFuture的返回值就直接返回。
     *
     * @param serviceName
     * @param service
     * @param intfClazz
     * @return
     */
    public static LegacyRpcDecorator create(String serviceName, Object service, Class<?> intfClazz, Executor executor) {
       if (intfClazz == null) {
            throw new IllegalArgumentException("intfClazz must not null");
        }

        if (StringUtils.isNullOrEmpty(serviceName)) {
            RpcService rpcAnno = intfClazz.getAnnotation(RpcService.class);
            if (rpcAnno == null || StringUtils.isNullOrEmpty(rpcAnno.value())) {
                serviceName = intfClazz.getSimpleName();
            } else {
                serviceName = rpcAnno.value();
            }
        }


        return new LegacyRpcDecorator(service, serviceName, intfClazz, executor);
    }

    private LegacyRpcDecorator(Object service, String name, Class<?> intfClazz, Executor executor) {
        super(name, true);

        // 初始化 RpcMethod
        for (Method method : intfClazz.getMethods()) {

            RpcMethod anno = method.getAnnotation(RpcMethod.class);
            String methodName;
            if (anno != null && !anno.value().isEmpty()) {
                methodName = anno.value();
            } else {
                methodName = method.getName();
            }

            RpcServerMethodHandler mh = createMethodHandler(service, method);
            mh.setExecutor(executor);
            this.addMethodHandler(methodName, mh);
        }
    }

    private static RpcServerMethodHandler createMethodHandler(final Object target, final Method method) {
        Class<?>[] params = method.getParameterTypes();
        if (params.length != 1) {
            throw new IllegalArgumentException("legacyRpcService method only support 1 args");
        }

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
