package org.helium.http.servlet.spi;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.helium.http.servlet.StaticResourceModule;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lei Gao on 7/23/15.
 */
public class StaticResourceHandler extends HttpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(StaticResourceModule.class);

	private Object syncRoot;
	private String requestRoot;
	private String resourceRoot;
	private String defaultContent;
	private Class<?> rootClazz;

	private String contextPath;

	private Map<String, Entry> entrys;

	public StaticResourceHandler(String contextPath, String resourceRoot, Class<?> clazz)
	{
		this.contextPath = contextPath;
//		this.requestRoot = requestRoot;
		this.resourceRoot = resourceRoot;
//		if (!this.resourceRoot.endsWith("/")) {
//			this.resourceRoot = this.resourceRoot + "/";
//		}
		if (!this.resourceRoot.startsWith("/")) {
			this.resourceRoot = "/" + this.resourceRoot;
		}
		this.rootClazz = clazz;
		entrys = new HashMap<String, Entry>();

		if (defaultContent != null) {
			this.defaultContent = defaultContent;
		} else {
			this.defaultContent = "";
		}
	}

	@Override
	public void service(Request request, Response response) throws Exception {
		LOGGER.info("http request received {}", request.getContextPath());
		String relativePath = HttpUtils.getRelativePath(request.getRequestURI(), contextPath);

		Entry entry;
		synchronized (this) {
			entry = entrys.get(relativePath);
			if (entry == null) {
				entry = loadEntry(relativePath);
				if (entry != null) {
					entrys.put(relativePath, entry);
				}
			}
		}

//
//		if (StringUtils.isNullOrEmpty(relativePath)) {
//			if (!"".equals(defaultContent)) {
////				String redirect = request.getRequestURI().getRawPath() + defaultContent;
////				e.getResponseHeaders().add("Location", redirect);
////				e.sendResponseHeaders(302, 0);
////				e.close();
////				LOGGER.info("redirect to {}", redirect);
//				return;
//			}
//		}
//

		if (entry == null) {
			response.sendError(404);
		} else if (entry.statusCode != 200) {
			response.sendError(entry.statusCode);
		} else {
			response.setStatus(200);
			response.setContentLength(entry.buffer.length);
			OutputStream stream = response.getOutputStream();
			stream.write(entry.buffer, 0, entry.buffer.length);
			stream.close();
		}
		LOGGER.info("request {} get {}", request.getRequestURI(), entry.statusCode);
	}

	private Entry loadEntry(String relativePath)
	{
		Bundle bundle;
		Entry entry;
		String path = resourceRoot + relativePath;
		URL url = rootClazz.getResource(path);

		LOGGER.info("loading path={}", path);
		if (url == null) {
			return null;
		}
		try {
			InputStream in = rootClazz.getResourceAsStream(path);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			while (true) {
				int len = in.read(buffer, 0, buffer.length);
				if (len > 0) {
					out.write(buffer, 0, len);
				} else {
					break;
				}
			}
			entry = new Entry();
			entry.statusCode = 200;
			entry.buffer = out.toByteArray();
			LOGGER.info("load {} into cache", relativePath);
			return entry;
		} catch (IOException ex) {
			LOGGER.error("load stream failed {}:" + relativePath, ex);
			entry = new Entry();
			entry.statusCode = 500;
			return entry;
		}
	}

	private static class Entry {
		private int statusCode;
		private byte[] buffer;
	}
}
