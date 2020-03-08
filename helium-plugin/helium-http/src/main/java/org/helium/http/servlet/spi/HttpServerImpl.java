package org.helium.http.servlet.spi;

import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.util.StringUtils;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.jaxws.JaxwsHandler;
import org.glassfish.grizzly.servlet.FilterRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.helium.framework.BeanContext;
import org.helium.framework.configuration.Environments;
import org.helium.framework.servlet.ServletMappings;
import org.helium.framework.spi.ModuleInstance;
import org.helium.framework.spi.ServletInstance;
import org.helium.http.servlet.HttpModule;
import org.helium.http.servlet.HttpServletMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;
import java.util.*;

/**
 * Created by Lei Gao on 8/25/15.
 */
public class HttpServerImpl {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerImpl.class);
	private static final String NAME = "Helium-" + Environments.FRAMEWORK_VERSION;

	private HttpServer server;
	private ServerConfiguration configuration;
	private Map<String, ContextPathWrapper> contextWrappers;
	private Map<String, HttpHandler> wsHandlers;
	private ContextPathWrapper rootNode;

	public HttpServerImpl() {
		server = new HttpServer();
		configuration = server.getServerConfiguration();
//		RootHttpHandler handler = new RootHttpHandler();
//		configuration.addHttpHandler(handler, handler.getRegistration());
		contextWrappers = new HashMap<>();
		wsHandlers = new HashMap<>();

		PerformanceCounterFactory.getCounters(HttpServletCounters.class, "-");
	}

	public void addHttpListener(String host, int port) {
		LOGGER.info(">>> add http Listener {}:{}", host, port);
		NetworkListener listener = new NetworkListener(NAME, host, port);
		server.addListener(listener);
	}


	public void addHttpListener(String host, int port,int maxPoolSize) {
		LOGGER.info(">>> add http Listener {}:{}", host, port);
		NetworkListener listener = new NetworkListener(NAME, host, port);
		//手动设置工作线程数
		listener.getTransport().getWorkerThreadPoolConfig().setMaxPoolSize(maxPoolSize);
		server.addListener(listener);
	}



	public void addHttpsListener(String host, int port, String keyStoreFilePath, String keyStorePassword, String keyPassword, String keyStoreType) {
		LOGGER.info(">>> add https Listener {}:{}", host, port);
		HttpsKeyStore ks = new HttpsKeyStore();
		NetworkListener listener = ks.createListener(host, port, keyStoreFilePath, keyStorePassword, keyPassword, keyStoreType);
		server.addListener(listener);
	}

	public void start() throws Exception {
		try {
			server.start();
			for (NetworkListener listener : server.getListeners()) {
				listener.start();
			}
		} catch (Exception ex) {
			throw new RuntimeException("HttpServer started in %s failed", ex);
		}
	}

	public void stop() {
		for (NetworkListener listener: server.getListeners()) {
			listener.shutdown();
		}
	}

	public void registerModule(BeanContext context) {
		ModuleInstance instance = (ModuleInstance)context;
		HttpModule module = (HttpModule) instance.getBean();

        String contextPath = module.getContextPath();
		ContextPathWrapper wrapper = getContextWrapper(contextPath);
		wrapper.registerModule(module);
//
//        LOGGER.info("registerModule context={} module={}", contextPath, module.getClass().getName());
//
//		node.rootFilter.addModule(module);
//		node.deploy(server);
	}

	public void registerServlet(BeanContext context) {
		ServletInstance instance = (ServletInstance)context;

		HttpServlet servlet = (HttpServlet)context.getBean();
		HttpServletMappings mappings = (HttpServletMappings)context.getAttachment(ServletMappings.class);

		org.helium.http.servlet.HttpServlet servlet2;
		if (servlet instanceof org.helium.http.servlet.HttpServlet) {
			servlet2 = (org.helium.http.servlet.HttpServlet)servlet;
			servlet2.setServletName(context.getId().toString());
			HttpServletMappings m2 = servlet2.getMappings();
			if (m2 != null) {
				mappings = m2;
			}
		} else {
			servlet2 = new HttpServletWrapper(servlet, null, context.getId().toString());
		}


		String contextPath = mappings.getContextPath();
		ContextPathWrapper wrapper = getContextWrapper(contextPath);
		wrapper.registerServlet(servlet2, mappings.getUrlPattern());
//
//		LOGGER.info("register Servlet context={} servlet={}", contextPath, servlet);
//		HttpServletWrapper wrapper = new HttpServletWrapper(servlet, instance.getModuleChain(), context.getId().toString());
//		wrapper.setExecutor(instance.getExecutor());
//		// wrapper.setModuleHandler(node.rootFilter);
//		wrapper.setUrlPattern(mappings.getUrlPattern());
//		node.servlets.add(wrapper);
//		node.deploy(server);
	}

	public void unregisterModule(BeanContext context) {
//		ModuleInstance instance = (ModuleInstance)context;
//		HttpModule module = (HttpModule) context.getBean();
//
//		String contextPath = module.getContextPath();
//		ContextPathNode node = getContextNode(contextPath);
//		LOGGER.info("unregisterModule context={} module={}", contextPath, module.getClass().getName());
//
//		node.rootFilter.removeModule(module);
		throw new UnsupportedOperationException("NotImplementation");
	}

	public void unregisterServlet(BeanContext context) {
		throw new UnsupportedOperationException("NotImplementation");
	}

	private ContextPathWrapper getContextWrapper(String contextPath) {
		if (StringUtils.isNullOrEmpty(contextPath)) {
			contextPath = "/";
		}
		if (!contextPath.startsWith("/")) {
			throw new IllegalArgumentException("contextPath must start with '/' :" + contextPath);
		}
		synchronized (this) {
			ContextPathWrapper wrapper = contextWrappers.get(contextPath);
			if (wrapper == null) {
				wrapper = new ContextPathWrapper(contextPath);
				wrapper.deploy(server);
//				if ("/".equals(contextPath)) {
//					node.appContext = new WebappContext(contextPath);
//					node.rootFilter = new ContextPathRootFilter("");
//					node.rootServlet = new RootHttpServlet();
//				} else {
//					node.appContext = new WebappContext(contextPath, contextPath);
//					node.rootFilter = new ContextPathRootFilter(contextPath);
//					node.rootServlet = new RootHttpServlet();
//				}
				contextWrappers.put(contextPath, wrapper);
			}
			return wrapper;
		}	
	}

	public void registerWebService(Object wsObject, String wsPath) {
		synchronized (this) {
			HttpHandler wsHandler = new JaxwsHandler(wsObject);
			wsHandlers.put(wsPath, wsHandler);
			configuration.addHttpHandler(wsHandler, wsPath);
		}
	}

	public boolean unregisterWebService(String wsPath) {
		synchronized (this) {
			HttpHandler wsHandler = wsHandlers.get(wsPath);
			if (wsHandler != null) {
				configuration.removeHttpHandler(wsHandler);
				return true;
			} else {
				return false;
			}
		}
	}

	public void deploy(HttpServer server, String contextPath) {
//		WebappContext appContext = new WebappContext(contextPath);
//		// appContext.undeploy();
//		
//		
//		appContext.undeploy();
//		FilterRegistration fr = appContext.addFilter("rootFilter", rootFilter);
//		fr.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
//
//		int n = 0;
//		boolean needRootServlet = true;
//		for (HttpServletWrapper wrapper: servlets) {
//			if ("/*".equals(wrapper.getUrlPattern())) {
//				needRootServlet = false;
//			}
//		}
//		if (needRootServlet) {
//			ServletRegistration sr = appContext.addServlet("servlet" + n++, rootServlet);
//			sr.addMapping("/*");
//		}
//
//		for (HttpServletWrapper wrapper: servlets) {
//			ServletRegistration sr = appContext.addServlet("servlet" + n++, wrapper);
//			sr.addMapping(wrapper.getUrlPattern());
//			LOGGER.info("add Servlet {}", wrapper.getUrlPattern());
//		}
//		appContext.deploy(server);
//
		server.getServerConfiguration().getHttpHandlersWithMapping().forEach((k, v) -> {
			StringBuilder str = new StringBuilder();
			for (int i = 0; i < v.length; i++) {
				str.append(String.format("contextPath=%s urlPattern=%s\r\n", v[i].getContextPath(), v[i].getUrlPattern()));
			}
			LOGGER.warn("SERVER_HANDLER: handler={} mappings=\r\n{}", k.getName(), str.toString());
		});
	}
}
