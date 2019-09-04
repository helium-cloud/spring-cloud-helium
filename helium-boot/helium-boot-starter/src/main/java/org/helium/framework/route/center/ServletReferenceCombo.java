package org.helium.framework.route.center;

import org.helium.framework.BeanContext;
import org.helium.framework.BeanContextState;
import org.helium.framework.BeanIdentity;
import org.helium.framework.BeanType;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.entitys.BeanConfiguration;
import org.helium.framework.module.ModuleContext;
import org.helium.framework.route.ServerEndpoint;
import org.helium.framework.route.ServerRouter;
import org.helium.framework.route.ServerUrl;
import org.helium.framework.route.abtest.Factor;
import org.helium.framework.servlet.ServletContext;
import org.helium.framework.servlet.ServletMatchResult.Filter;
import org.helium.framework.servlet.ServletMatchResults;
import org.helium.framework.spi.ServletReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 处理灰度及不同版本下的同步
 * 当前的处理方式存在的问题：

 * Created by Coral on 8/6/15.
 */
public class ServletReferenceCombo implements ServletContext {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServletReferenceCombo.class);

	private BeanIdentity id;
	private String protocol;
	private List<RefNode> primaries;
	private List<RefNode> experiments;
	private Map<String, ServerRouter> routers;
	private BeanConfiguration configuration;
	private BeanContextProvider contextProvider;

	public ServletReferenceCombo(BeanConfiguration bc, BeanContextProvider contextProvider) {
		this.id = BeanIdentity.parseFrom(bc.getId());
		this.protocol = bc.getServletMappings().getProtocol();
		this.configuration = bc;

		primaries = new ArrayList<>();
		experiments = new ArrayList<>();
		routers = new HashMap<>();
		this.contextProvider = contextProvider;
	}

	@Override
	public String getProtocol() {
		return protocol;
	}

	@Override
	public ServletMatchResults matchRequest(ModuleContext ctx, Filter filter, Object... args) {
		ServletMatchResults results = new ServletMatchResults();
		//
		// TODO：这里的逻辑有些低效，但因为基本都是CPU计算，所以当Servlet+Experiments节点<100时没有问题，将来需要考虑优化
		synchronized (this) {
			//
			// 如果可能进行灰度记录的匹配则，将匹配的灰度节点加入结果
			if (ctx != null) {
				for (RefNode ref : experiments) {
					if (ref.factor.apply(ctx)) {
						ServletMatchResults r2 = ref.servlet.matchRequest(ctx, filter, args);
						if (r2.hasResult()) {
							results.addResults(r2);
						}
					}
				}
			}
			if (results.getResults() != null) {
				results.getResults().forEach(r -> r.setIsExperiment(true));
			}

			//
			// 针对正常发布节点，根据权值随机获取
			for (RefNode ref : primaries) {
				ServletMatchResults r2 = ref.servlet.matchRequest(ctx, filter, args);
				results.addResults(r2);
			}
		}
		return results;
	}

	/**
	 * 增加主版本
	 * @param version
	 * @param bc
	 */
	public void addPrimary(String version, BeanConfiguration bc, String bundleName) {
		synchronized (this) {
			// ServerRouter router = getRouter(version);
			RefNode node = new RefNode(version, bc);
			ServerRouter router = BeanContext.getContextService().subscribeServerRouter(node.servlet, bundleName, node.servlet.getProtocol());
			node.servlet.setRouter(router);

			// router.setBeanContext(node.servlet);
			primaries.add(node);
			updateConfiguration();
		}
	}

	/**
	 * 移除主版本
	 * @param version
	 */
	public void removePrimary(String version) {
		synchronized (this) {
			primaries.removeIf(n -> version.equals(n.version));
			updateConfiguration();
		}
	}


	public void addExperiment(String version, BeanConfiguration bc, Factor factor, ServerEndpoint server) {
		ServerUrl url = server.getServerUrl(protocol);
		synchronized (this) {
			RefNode ref = new RefNode(version, bc);
			ServerRouter router = new ServerRouter() {
				@Override
				public int getWeight() {
					return 1;
				}

				@Override
				public BeanContext getBeanContext() {
					return ref.servlet;
				}

				@Override
				public ServerUrl pickServer() {
					return url;
				}

				@Override
				public ServerUrl pickServer(String tag) {
					return url;
				}

				@Override
				public boolean hasServer(ServerUrl a) {
					return url.equals(a);
				}

				@Override
				public List<ServerUrl> getAllUrls() {
					return Arrays.asList(url);
				}
			};

			ref.factor = factor;
			ref.serverId = server.getId();
			ref.servlet.setRouter(router);

			experiments.add(ref);
		}
	}

	public void removeExperiment(String version, ServerEndpoint server) {
		synchronized (this) {
			experiments.removeIf(ref ->	ref.version.equals(version) && ref.serverId.equals(server.getId()));
		}
	}

	public boolean isEmpty() {
		return primaries.isEmpty() && experiments.isEmpty();
	}

	private void updateConfiguration() {
		String version = "";
		BeanConfiguration bc = null;
		for (RefNode node: primaries) {
			if (node.version.compareTo(version) > 0) {
				bc = node.servlet.getConfiguration();
			}
		}
		if (bc != null) {
			configuration = bc;
			return;
		}
		for (RefNode node: experiments) {
			if (node.version.compareTo(version) > 0) {
				bc = node.servlet.getConfiguration();
			}
		}
		if (bc != null) {
			configuration = bc;
		}
	}

	@Override
	public boolean isLocal() {
		return false;
	}

	@Override
	public BeanIdentity getId() {
		return id;
	}

	@Override
	public BeanType getType() {
		return BeanType.SERVLET;
	}

	@Override
	public Object getBean() {
		return null;
	}

	@Override
	public BeanConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public BeanContextState getState() {
		return BeanContextState.RESOLVED;
	}

	@Override
	public Throwable getLastError() {
		return null;
	}

	private Map<String, Object> attachments = new HashMap<>();
	@Override
	public Object putAttachment(String key, Object value) {
		synchronized (this) {
			return attachments.put(key, value);
		}
	}

	@Override
	public Object getAttachment(String key) {
		synchronized (this) {
			return attachments.get(key);
		}
	}

	/**
	 * 提供给GetBeansServlet读取服务信息使用
	 * @return
	 */
	public List<ServerUrl> getServiceUrls() {
		List<ServerUrl> urls = new ArrayList<>();
		for (RefNode expr: experiments) {
			List<ServerUrl> list = expr.servlet.getRouter().getAllUrls();
			for (ServerUrl u2: list) {
				u2 = ServerUrl.parse(u2.toString());
				u2.putParameters("gray", expr.factor.toString());
				urls.add(u2);
			}
		}

		for (RefNode primary: primaries) {
			List<ServerUrl> list = primary.servlet.getRouter().getAllUrls();
			for (ServerUrl u2: list) {
				urls.add(u2);
			}
		}
		return urls;
	}

	private static class RefNode {
		private String version;
		private Factor factor;
		private ServletReference servlet;
		private String serverId;

		RefNode(String version, BeanConfiguration bc) {
			this.version = version;
			this.servlet = new ServletReference(bc);
		}
	}

}