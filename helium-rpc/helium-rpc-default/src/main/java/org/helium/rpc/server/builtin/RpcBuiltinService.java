/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2012-3-2
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.server.builtin;

import org.helium.rpc.server.RpcMethod;
import org.helium.rpc.server.RpcServerContext;
import org.helium.rpc.server.RpcServiceBase;
import org.helium.rpc.server.RpcServiceBootstrap;
import org.helium.util.ServiceEnviornment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rpc内置服务
 * <p>
 * Created by Coral
 */
public class RpcBuiltinService extends RpcServiceBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcBuiltinService.class);

	public RpcBuiltinService() {
		super("__Builtin");
	}

	@RpcMethod("Ping")
	public void ping(RpcServerContext ctx) {
		try {
			RpcPingResults results = new RpcPingResults();
			results.setServerName(ServiceEnviornment.getComputerName());
			String serviceName = results.getServiceName();
			serviceName = serviceName == null ? System.getProperty("sun.java.command") : serviceName;
			results.setServiceName(serviceName);
			results.setServices(RpcServiceBootstrap.INSTANCE.getRunningService());
			ctx.end(results);
		} catch (Exception e) {
			LOGGER.error("__Builtin.Ping failed", e);
			ctx.end(e);
		}
	}

	/**
	 * 枚举，包含类型信息 类似于生成中间类型
	 *
	 * @param ctx
	 * @RpcMethod("EnumService") {在这里补充功能说明}
	 */
	public void enumService(RpcServerContext ctx) {
	}
}
