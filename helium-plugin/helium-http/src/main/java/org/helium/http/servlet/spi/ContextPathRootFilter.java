package org.helium.http.servlet.spi;

import org.helium.framework.module.ModuleChain;
import org.helium.framework.module.ModuleState;
import org.helium.http.servlet.HttpModule;
import org.helium.http.servlet.HttpServletContext;
import org.helium.util.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Lei Gao on 8/25/15.
 */
class ContextPathRootFilter implements Filter {
	private static final Logger LOGGER = LoggerFactory.getLogger(ContextPathRootFilter.class);

	private String contextPath;
	private ModuleChain chain;
	private ModuleChain chain2;

	public ContextPathRootFilter(String contextPath) {
		this.contextPath = contextPath;
		this.chain = new ModuleChain();
		this.chain2 = chain.clone();
	}

	public void addModule(HttpModule module) {
		synchronized (this) {
			chain.addModule(module);
			chain2 = chain.clone();
		}
	}

	public void removeModule(HttpModule module) {
		synchronized (this) {
			chain.removeModule(module);
			chain2 = chain.clone();
		}
	}

//	public void processModuleChain(HttpServletContext ctx, Action<ModuleState> callback) {
//		this.chain2.processModuleChain(ctx, new Action<ModuleState>() {
//			@Override
//			public void run(ModuleState a) {
//				if (!a.isTerminated()) {
//					try {
//						callback.run(a);
//					} catch (Exception ex) {
//						LOGGER.error("doFilter failed {}", ex);
//					}
//				}
//			}
//		});
//	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletContext ctx = new HttpServletContext((HttpServletRequest)request, (HttpServletResponse)response);
		this.chain2.processModuleChain(ctx, new Action<ModuleState>() {
			@Override
			public void run(ModuleState a) {
				if (!a.isTerminated()) {
					try {
						chain.doFilter(request, response);
					} catch (Exception ex) {
						LOGGER.error("doFilter failed {}", ex);
					}
				}
			}
		});
	}

	@Override
	public void destroy() {
	}
}
