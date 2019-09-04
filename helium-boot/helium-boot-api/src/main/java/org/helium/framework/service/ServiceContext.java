package org.helium.framework.service;

import org.helium.framework.BeanContext;
import org.helium.framework.BeanType;
import org.helium.framework.module.ModuleContext;

/**
 * 用于处理全量及灰度路由
 */
public interface ServiceContext extends BeanContext {
	@Override
	default BeanType getType() {
		return BeanType.SERVICE;
	}

	/**
	 * 确定一个Bean所支持的protocol
	 * 这个protocol的概念是RPC
	 * 对于整个平台来讲, protocol的确定规范是可执行的
	 * @return
	 */
	String getProtocol();

	/**
	 * 确定一个Bean所支持的protocol,
	 * 这个protocol的概念是RPC
	 * 对于整个平台来讲, protocol的确定规范是可执行的
	 * @return
	 */
	String getAdapterTag();


	/**
	 * 匹配请求结果, 如果匹配请求结果为
	 * @param args
	 * @return
	 */
	ServiceMatchResults matchRequest(ModuleContext ctx, Object... args);


}
