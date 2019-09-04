package org.helium.framework.spi;

import org.helium.framework.BeanContext;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.entitys.BeanConfiguration;
import org.helium.framework.entitys.ServletMappingsNode;
import org.helium.framework.module.ModuleContext;
import org.helium.framework.route.ServerRouter;
import org.helium.framework.route.ServerUrl;
import org.helium.framework.servlet.*;
import org.helium.framework.servlet.ServletMatchResult.Filter;

import java.util.ArrayList;
import java.util.List;

// import org.helium.framework.route.StaticServerRouter;

/**
 * Created by Coral on 7/28/15.
 */
public class ServletInstance extends BeanInstance implements ServletContext {
	private ServletMappings mappings;
	private ServletDescriptor descriptor;
	private ServletStack[] stacks;
	private ServerRouter router;

	public ServletInstance(BeanConfiguration configuration, BeanContextProvider cp) {
		super(configuration, cp);
		//this.router = new StaticServerRouter();
		//this.router.setBeanContext(this);
	}

	@Override
	public String getProtocol() {
		return descriptor.getProtocol();
	}

	@Override
	public ServletMatchResults matchRequest(ModuleContext ctx, Filter filter, Object... args) {
		// 不支持灰度发布，所以不处理ctx
		ServletMatchResult result = mappings.match(filter, args);
		if (result.isMatch()) {
			result.setRouter(router);
		}
		return new ServletMatchResults(result);
	}

//	@Override
//	public ServletMatchResult matchFirst(ModuleContext ctx, Filter filter, Object... args) {
//		return null;
//	}

	@Override
	public void doResolve() {
		StackManager servletService = BeanContext.getContextService().getService(StackManager.class);
		ServletMappingsNode node = getConfiguration().getServletMappings();

		if (node == null) {
			throw new IllegalArgumentException("Servlet must have <servletMappings/> node");
		}

		descriptor = servletService.getServletDescriptor(getBean());
		if (descriptor == null) {
			String msg = "unknown module or servlet:" + this.toString() + " missing ServletStack check <stacks/> node";
			throw new IllegalArgumentException(msg);
		}

		this.putAttachment(ServletDescriptor.class, descriptor);
		mappings = descriptor.parseMappings(getConfiguration().getServletMappings());
		this.putAttachment(ServletMappings.class, mappings);

		//
		// 初始化Stacks
		List<String> ss = getConfiguration().getParentNode().getStacks();
		stacks = new ServletStack[ss.size()];
		for (int i = 0; i < ss.size(); i++) {
			stacks[i] = servletService.getStack(ss.get(i));
			if (stacks[i] == null) {
				throw new IllegalArgumentException("unknown stacks:" + ss.get(i));
			}
		}
	}

	@Override
	protected void doStart() {
		List<ServerUrl> urls = new ArrayList<>();
		for (ServletStack stack: stacks) {
			stack.registerServlet(this);
			stack.getServerUrls().forEach(url -> urls.add(url));
		}

		router = new ServerRouter() {
			@Override
			public int getWeight() {
				return urls.size();
			}

			@Override
			public BeanContext getBeanContext() {
				return ServletInstance.this;
			}

			@Override
			public ServerUrl pickServer() {
				return urls.get(0);
			}

			@Override
			public ServerUrl pickServer(String tag) {
				return urls.get(0);
			}

			@Override
			public boolean hasServer(ServerUrl a) {
				for (ServerUrl url: urls) {
					if (url.equals(a)) {
						return true;
					}
				}
				return false;			}

			@Override
			public List<ServerUrl> getAllUrls() {
				return urls;
			}
		};
	}

	@Override
	protected void doStop() {
		for (ServletStack stack: stacks) {
			stack.unregisterServlet(this);
		}
	}
}

// TODO: ServletDescriptor获取规范
//		String protocol = node.getProtocol();
//		StackManager scs = BeanContext.getContextService().getService(StackManager.class);
//		ServletDescriptor descriptor;
//		if (!StringUtils.isNullOrEmpty(protocol)) {
//			descriptor = scs.getDescriptor(protocol);
//		} else {
//			descriptor = scs.getDescriptor(getBean());
//		}
//		if (descriptor == null) {
//			throw new IllegalArgumentException("Unknown servlet:" + bean.getClass());
//		}