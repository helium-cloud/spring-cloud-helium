package org.helium.http.servlet.spi;

import org.helium.framework.module.ModuleChain;
import org.helium.framework.module.ModuleState;
import org.helium.http.servlet.HttpModule;
import org.helium.http.servlet.HttpServlet;
import org.helium.http.servlet.HttpServletContext;
import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.Stopwatch;
import org.helium.util.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.Executor;

/**
 * 将Java的HttpServlet封装到helium支持的servlet上
 * Created by Lei Gao on 7/24/15.
 */
public class HttpServletWrapper extends HttpServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpServletWrapper.class);

	private Executor executor;
	private javax.servlet.http.HttpServlet servlet;
	private HttpServletCounters counter;
	private ModuleChain moduleChain;
	private String urlPattern;

	public HttpServletWrapper(javax.servlet.http.HttpServlet servlet, ModuleChain mc, String servletName) {
		super();
		this.servlet = servlet;
		this.counter = PerformanceCounterFactory.getCounters(HttpServletCounters.class, servletName);
		if (moduleChain != null) {
			this.moduleChain = mc;
		} else {
			this.moduleChain = new ModuleChain();
		}
	}

	public void addModule(HttpModule module) {
		moduleChain.addModule(module);
	}

	public String getUrlPattern() {
		return urlPattern;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}

	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		HttpServletContext ctx = new HttpServletContext((HttpServletRequest) req, (HttpServletResponse) res);
		//counter.getRequest().increase();
		counter.getThroughput().increaseBy(req.getContentLength());

		Runnable r = new Runnable() {
			@Override
			public void run() {
				moduleChain.processModuleChain(ctx, new Action<ModuleState>() {
					@Override
					public void run(ModuleState a) {
						Stopwatch w2 = counter.getTx().begin();
						if (!a.isTerminated()) {
							try {
								servlet.service(ctx.getRequest(), ctx.getResponse());
								w2.end();
							} catch (Exception e) {
								LOGGER.error("Servlet:" + servlet.getServletName() + "process Failed {}", e);
								w2.fail(e);
								ctx.sendError(500, e);
							}
						}
					}
				});
			}
		};

		if (executor == null) {
			r.run();
		} else {
			AsyncContext ac = req.startAsync();
			ac.addListener(new AsyncListener() {
				@Override
				public void onComplete(AsyncEvent event) throws IOException {
					LOGGER.info("async onComplete");
				}

				@Override
				public void onTimeout(AsyncEvent event) throws IOException {
					LOGGER.info("async onTimeout");
				}

				@Override
				public void onError(AsyncEvent event) throws IOException {
					LOGGER.info("async onError");
				}

				@Override
				public void onStartAsync(AsyncEvent event) throws IOException {
					LOGGER.info("async onStartAsync");
				}
			});
			ac.setTimeout(30000);
			executor.execute(r);
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		servlet.init(config);
	}

	@Override
	public void init() throws ServletException {
		servlet.init();
	}

	@Override
	public void process(HttpServletContext ctx) throws Exception {
		servlet.service(ctx.getRequest(), ctx.getResponse());
	}

	@Override
	public void destroy() {
		servlet.destroy();
	}

	@Override
	public String getInitParameter(String name) {
		return servlet.getInitParameter(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return servlet.getInitParameterNames();
	}

	@Override
	public ServletConfig getServletConfig() {
		return servlet.getServletConfig();
	}

	@Override
	public ServletContext getServletContext() {
		return servlet.getServletContext();
	}

	@Override
	public String getServletInfo() {
		return servlet.getServletInfo();
	}

	@Override
	public void log(String msg) {
		servlet.log(msg);
	}

	@Override
	public void log(String message, Throwable t) {
		servlet.log(message, t);
	}

	@Override
	public String getServletName() {
		return servlet.getServletName();
	}

	public Executor getExecutor() {
		return executor;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}
}
