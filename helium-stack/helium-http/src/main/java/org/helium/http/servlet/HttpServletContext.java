package org.helium.http.servlet;

import org.helium.framework.configuration.Environments;
import org.helium.framework.module.AbstractModuleContext;
import org.helium.framework.module.ModuleContext;
import org.helium.http.logging.LogUtils;
import org.helium.http.utils.UrlPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * HttpServletRequest,HttpServletResponse的友好封装, 提供更多的便捷接口
 * 
 * Created by Coral on 7/6/15.
 */
public class HttpServletContext extends AbstractModuleContext implements ModuleContext {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpServletContext.class);

	private String contextPath;
	private UrlPattern pattern;
	private HttpServletRequest request;
	private HttpServletResponse response;

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public UrlPattern getPattern() {
		return pattern;
	}

	public void setPattern(UrlPattern pattern) {
		this.pattern = pattern;
	}

	public HttpServletContext(HttpServletRequest req, HttpServletResponse resp) {
		this.request = req;
		this.response = resp;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void sendError(int sc) {
		try {
			response.sendError(sc);
		} catch (Exception ex) {
			LOGGER.error("HttpServletContext end failed: {}", ex);
		}
	}

	public void sendError(Throwable error) {
		sendError(500, error);
	}

	public void sendError(int sc, Throwable e) {
		try {
			response.setStatus(sc);
			if ("true".equals(Environments.getVar("HTTP_DEBUG"))) {
				String message = LogUtils.formatError(e);
				response.getOutputStream().print(message);
				response.getOutputStream().close();
			}
		} catch (Exception ex) {
			LOGGER.error("sendContent failed!, {}", ex);
		}
	}

	public void sendContent(String contentType, byte[] buffer) {
		try {
			response.setContentType(contentType);
			response.getOutputStream().write(buffer);
			response.getOutputStream().close();
		} catch (Exception ex) {
			LOGGER.error("sendContent failed!, {}", ex);
		}
	}
}
