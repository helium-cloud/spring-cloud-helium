package org.helium.framework.servlet;

import org.helium.framework.BeanContext;
import org.helium.framework.BeanType;
import org.helium.framework.module.ModuleContext;
import org.helium.framework.route.ServerRouter;

import java.util.function.Predicate;

/**
 * Created by Coral on 8/8/15.
 */
public interface ServletContext extends BeanContext {
	@Override
	default BeanType getType() {
		return BeanType.SERVLET;
	}

	/**
	 * 确定一个Bean所支持的protocol,
	 * 这个protocol的概念是http, sip...
	 * 对于整个平台来讲, protocol的确定规范是可执行的
	 * @return
	 */
	String getProtocol();

	/**
	 * 匹配请求结果, 如果匹配请求结果为
	 * @param args
	 * @return
	 */
	ServletMatchResults matchRequest(ModuleContext ctx, ServletMatchResult.Filter filter, Object... args);

	/**
	 * 返回第一个匹配结果, 仅使用Filter.applyFirst
	 * @param ctx
	 * @param filter
	 * @param args
	 * @return
	 */
	// ServletMatchResult matchFirst(ModuleContext ctx, ServletMatchResult.Filter filter, Object... args);
}
