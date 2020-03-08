package org.helium.http.servlet;

import org.helium.util.StringUtils;
import org.glassfish.grizzly.http.util.MimeType;
import org.helium.framework.annotations.ServletImplementation;
import org.helium.framework.tag.Initializer;
import org.helium.http.servlet.spi.HttpUtils;
import org.helium.http.servlet.spi.StaticResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Created by Lei Gao on 7/7/15.
 */
@ServletImplementation(id = "http:static_${AUTO_GUID}")
@HttpMappings(contextPath = "/", urlPattern = "/*")
public class StaticResourceServlet extends HttpServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(StaticResourceServlet.class);

	private String contextPath = "";
	private String resourceRoot = "/webroot";
	private String defaultContent = "index.html";
	private String extensions = "";

	private StaticResourceLoader resourceLoader;

	@Initializer
	public void initialize() {
		if (!contextPath.startsWith("/")) {
			contextPath = "/" + contextPath;
		}
		if (!resourceRoot.endsWith("/")) {
			resourceRoot = resourceRoot + "/";
		}
		HttpServletMappings mappings = new HttpServletMappings(contextPath, "/*");
		setMappings(mappings);
		resourceLoader = new StaticResourceLoader(resourceRoot, getClass());
	}

	@Override
	public void process(HttpServletContext ctx) throws Exception {
		String relativePath = HttpUtils.getRelativePath(ctx.getRequest().getRequestURI(), contextPath);
		if ("".equals(relativePath) || "/".equals(relativePath)) {
			String url = ctx.getRequest().getRequestURI();
			if (!url.endsWith("/")) {
				url = url + "/";
			}
			url = url + defaultContent;
			ctx.getResponse().sendRedirect(url);
			return;
		}
		byte[] buffer = resourceLoader.loadResource(relativePath);
		if (buffer != null) {
			try {
				String ext = StringUtils.getLastString(relativePath, ".");
				String contentType = MimeType.get(ext);
				ctx.sendContent(contentType, buffer);
			} catch (Exception e) {
				LOGGER.error("sendContent failed:", e);
			}
		}
	}
}
