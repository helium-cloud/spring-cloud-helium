package org.helium.framework.spi;

import org.helium.framework.BeanContext;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.entitys.BeanConfiguration;
import org.helium.framework.module.ModuleContext;
import org.helium.framework.route.ServerRouter;
import org.helium.framework.servlet.*;
import org.helium.framework.servlet.ServletMatchResult.Filter;
import org.helium.util.StringUtils;

/**
 * 处理单一Reference
 * Created by Coral on 8/8/15.
 */
public class ServletReference extends BeanReference implements ServletContext {
	private String protocol;
	private ServerRouter router;
	private ServletMappings mappings;

	public ServletReference(BeanConfiguration configuration) {
		this(configuration, null);
	}

	public ServletReference(BeanConfiguration configuration, BeanContextProvider cp) {
		super(configuration, cp);
	}

	public void setRouter(ServerRouter router) {
		this.router = router;
	}

	public void resolve() {
		protocol = getConfiguration().getServletMappings().getProtocol();
		if (StringUtils.isNullOrEmpty(protocol)) {
			throw new IllegalArgumentException("<servletMappings/> node must have protocol= attr");
		}

		StackManager service = BeanContext.getContextService().getService(StackManager.class);
		ServletDescriptor descriptor = service.getDescriptor(protocol);
		this.putAttachment(ServletDescriptor.class, descriptor); // Where to use

		this.mappings = descriptor.parseMappings(getConfiguration().getServletMappings());
	}

	@Override
	public String getProtocol() {
		return protocol;
	}

	@Override
	public ServletMatchResults matchRequest(ModuleContext ctx, Filter filter, Object... args) {
		ServletMatchResults results = new ServletMatchResults();
		ServletMatchResult result = mappings.match(filter, args);
		if (result.isMatch() && filter.applyFirst(result)) {
			result.setRouter(router);
			results.addResult(result);
		}
		return results;
	}

	@Override
	public ServerRouter getRouter() {
		return router;
	}

	@Override
	public Object getBean() {
		return null;
	}
}
