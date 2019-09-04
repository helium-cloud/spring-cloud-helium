package org.helium.http.servlet.spi;

import org.helium.perfmon.Stopwatch;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.WebappContext;
import org.helium.http.servlet.HttpModule;
import org.helium.http.servlet.HttpServlet;
import org.helium.http.servlet.HttpServletContext;
import org.helium.http.utils.UrlPattern;
import org.helium.http.utils.UrlPatternGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Coral on 1/12/17.
 */
class ContextPathWrapper extends javax.servlet.http.HttpServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(ContextPathWrapper.class);

	private String contextPath;

	private UrlPatternGroup servlets;
	private UrlPatternGroup modules;

	ContextPathWrapper(String contextPath) {
		this.contextPath = contextPath;
		this.servlets = new UrlPatternGroup();
		// this.modules = new UrlPatternGroup();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpServletContext ctx = new HttpServletContext(req, resp);
		String url = ctx.getRequest().getRequestURI();
		if (url.startsWith(contextPath)) {
			url = url.substring(contextPath.length());
		} else {
			ctx.sendError(new RuntimeException("Unexcepted Url:" + url));
		}

		UrlPattern pattern = servlets.match(url);
		ctx.setContextPath(contextPath);
		ctx.setPattern(pattern);

		if (pattern == null) {
			send404(req, resp);
		} else {
			HttpServlet servlet = (HttpServlet) pattern.getAttactment();
			HttpServletCounters counter = servlet.getCounter();
			Stopwatch w2 = null;
			if (counter != null) {
				//counter.getRequest().increase();
				counter.getThroughput().increaseBy(req.getContentLength());
				w2 = counter.getTx().begin();
			}
			try {
				servlet.process(ctx);
				if (w2 != null) {
					w2.end();
				}
			} catch (Exception ex) {
				ctx.sendError(ex);
				if (w2 != null) {
					w2.fail(ex);
				}
			}
		}
	}

	void registerServlet(HttpServlet servlet, String urlPattern) {
		UrlPattern pattern = UrlPattern.parse(urlPattern);
		pattern.setAttactment(servlet);
		UrlPatternGroup tmp = servlets.deepClone();
		tmp.addPattern(pattern);
		servlets = tmp;
	}

	void registerModule(HttpModule module) {

	}

	void deploy(HttpServer server) {
		WebappContext appContext = new WebappContext(contextPath, contextPath);
		ServletRegistration sr = appContext.addServlet("rootServlet", this);
		sr.addMapping("/*");
		appContext.deploy(server);


		LOGGER.warn("after deploy: {}", contextPath);
		server.getServerConfiguration().getHttpHandlersWithMapping().forEach((k, v) -> {
			StringBuilder str = new StringBuilder();
			for (int i = 0; i < v.length; i++) {
				str.append(String.format("contextPath=%s urlPattern=%s\r\n", v[i].getContextPath(), v[i].getUrlPattern()));
			}
			LOGGER.warn("SERVER_HANDLER: handler={} mappings=\r\n{}", k.getName(), str.toString());
		});

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
//		server.getServerConfiguration().getHttpHandlersWithMapping().forEach((k, v) -> {
//			StringBuilder str = new StringBuilder();
//			for (int i = 0; i < v.length; i++) {
//				str.append(String.format("contextPath=%s urlPattern=%s\r\n", v[i].getContextPath(), v[i].getUrlPattern()));
//			}
//			LOGGER.warn("SERVER_HANDLER: handler={} mappings=\r\n{}", k.getName(), str.toString());
//		});
	}

	private void send404(HttpServletRequest req, HttpServletResponse resp) {

	}
}
