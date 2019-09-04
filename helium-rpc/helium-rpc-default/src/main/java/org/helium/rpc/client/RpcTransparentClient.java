package org.helium.rpc.client;

import org.helium.rpc.channel.RpcClientTransaction;
import org.helium.rpc.channel.RpcEndpoint;
import org.helium.rpc.channel.RpcFuture;
import org.helium.rpc.channel.RpcResults;
import org.helium.threading.Future;
import org.helium.threading.FutureListener;
import org.helium.util.Result;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;

/**
 * Created by Coral on 8/23/15.
 */
public class RpcTransparentClient implements InvocationHandler {
	/**
	 * 创造一个透明调用Rpc的代理
	 *
	 * @param classLoader
	 * @param serviceName
	 * @param serviceInterface
	 * @param router
	 * @return
	 */
	public static Object createProxy(ClassLoader classLoader, String serviceName, Class<?> serviceInterface, Supplier<RpcEndpoint> router, int timeout) {
		RpcTransparentClient handler = new RpcTransparentClient(serviceName, serviceInterface, router, timeout);
		return Proxy.newProxyInstance(classLoader, new Class<?>[]{serviceInterface}, handler);
	}


	private String serviceName;
	private Supplier<RpcEndpoint> router;
	//Rpc超时时间
	private int timeout = -1;

	public RpcTransparentClient(String serviceName, Class<?> serviceInterface, Supplier<RpcEndpoint> router) {
		this.serviceName = serviceName;
		this.router = router;
	}

	public RpcTransparentClient(String serviceName, Class<?> serviceInterface, Supplier<RpcEndpoint> router, int timeout) {
		this.serviceName = serviceName;
		this.router = router;

		this.timeout = timeout;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if ("toString".equals(method.getName())) {
			return "RpcTransparentClient:" + serviceName;
		}

		RpcMethodStub stub = RpcProxyFactory.getMethodStub(router.get(), serviceName, method.getName());
		RpcClientTransaction tx = stub.createTransaction();
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				if (args[i] != null) {
					tx.putExtension(i, args[i]);
				}
			}
		}
		if (timeout > 200) {//默认防止设置果断影响业务,最小时间为200毫秒
			tx.setTimeout(timeout);
		}
		RpcFuture future = tx.begin();
		if (Future.class.isAssignableFrom(method.getReturnType())) {

			ParameterizedType pt = (ParameterizedType) method.getGenericReturnType();
			Class<?> returnType = (Class<?>) pt.getActualTypeArguments()[0];

			Future<?> f2 = new Future<>();
			future.addListener(new FutureListener<RpcResults>() {
				@Override
				public void run(Result<RpcResults> result) {
					if (result.getError() != null) {
						f2.complete(result.getError());
						return;
					}
					RpcResults r2 = result.getValue();
					if (r2.getError() != null) {
						f2.complete(r2.getError());
					} else {
						f2.complete(r2.getValue(returnType), null);
					}
				}
			});
			return f2;
		} else {
			RpcResults result = future.getValue();
			if (result.getError() != null) {
				throw result.getError();
			}
			return result.getValue(method.getReturnType());
		}
	}
}
