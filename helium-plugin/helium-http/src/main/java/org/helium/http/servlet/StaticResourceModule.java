package org.helium.http.servlet;

import org.helium.framework.annotations.ModuleImplementation;
import org.helium.framework.module.ModuleState;
import org.helium.framework.tag.Initializer;
import org.helium.http.servlet.spi.HttpUtils;
import org.helium.http.servlet.spi.StaticResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Created by Lei Gao on 7/7/15.
 */
@ModuleImplementation
public class StaticResourceModule implements HttpModule {
	private static final Logger LOGGER = LoggerFactory.getLogger(StaticResourceModule.class);

	private String contextPath = "";
	private String resourceRoot = "/webroot";
	private String exts = ""; //TODO: 扩展针对扩展名的
	private String defaultContent = "default.html";
	private StaticResourceLoader resourceLoader;

	@Initializer
	public void initialize() {
		if (!contextPath.startsWith("/")) {
			contextPath = "/" + contextPath;
		}
		resourceLoader = new StaticResourceLoader(resourceRoot, getClass());
	}

	@Override
	public String getContextPath() {
		return contextPath;
	}

	@Override
	public ModuleState processModule(HttpServletContext context) {
		String relativePath = HttpUtils.getRelativePath(context.getRequest().getRequestURI(), contextPath);
		byte[] buffer = resourceLoader.loadResource(relativePath);
		if (buffer != null) {
			try {
				context.sendContent(relativePath, buffer);
			} catch (Exception e) {
				LOGGER.error("sendContent failed:", e);
			}
			return ModuleState.newTerminated(null);
		} else {
			return ModuleState.newCompleted();
		}
	}

	@Override
	public boolean isMatch(HttpServletContext ctx) {
		String method = ctx.getRequest().getMethod();
		return "GET".equals(method);
	}
}
