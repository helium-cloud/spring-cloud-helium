package org.helium.http.servlet;


import org.helium.util.StringUtils;
import org.glassfish.grizzly.http.server.HttpHandlerRegistration;
import org.helium.framework.entitys.ServletMappingsNode;
import org.helium.framework.servlet.ServletMappings;
import org.helium.framework.servlet.ServletMatchResult;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.util.Base64;

/**
 * Created by Lei Gao on 7/8/15.
 */
public class HttpServletMappings implements ServletMappings {
	private String contextPath;
	private String urlPattern;
	private String urlPrefix;

	public String getContextPath() {
		return contextPath;
	}

	public String getUrlPattern() {
		return urlPattern;
	}

	public HttpServletMappings() {
	}

	public HttpServletMappings(String context, String urlPattern) {
		this.contextPath = context;
		this.urlPattern = urlPattern;

		if (!contextPath.startsWith("/")) {
			contextPath = "/" + contextPath;
		}
	}

	@Override
	public void initWithConfig(ServletMappingsNode node) {
		// 解决AnyNode序列化PB后会消失的问题
		String innerXml = node.getInnerXml();
		if (!StringUtils.isNullOrEmpty(innerXml)) {
			node = new ServletMappingsNode();
			node.parseXmlFrom(new String(Base64.getDecoder().decode(innerXml)));
		} else {
			node.setInnerXml(Base64.getEncoder().encodeToString(node.toXmlByteArray()));
		}
		HttpMappingsNode hm = node.getInnerNode(HttpMappingsNode.class);
		contextPath = hm.getContextPath();
		urlPattern = hm.getUrlPattern();
		urlPrefix = getPrefix(urlPattern);
	}

	@Override
	public void initWithAnnotation(Annotation a) {
		HttpMappings httpMappings = (HttpMappings)a;
		contextPath = httpMappings.contextPath();
		urlPattern = httpMappings.urlPattern();
		urlPrefix = getPrefix(urlPattern);
	}

	@Override
	public ServletMappingsNode getMappingsNode() {
		ServletMappingsNode node = HttpMappingsNode.createMappingsNode(contextPath, urlPattern);
		node.setInnerXml(Base64.getEncoder().encodeToString(node.toXmlByteArray()));
		return node;
	}

	@Override
	public ServletMatchResult match(ServletMatchResult.Filter filter, Object... args) {
		HttpServletRequest request = (HttpServletRequest)args[0];

		if (!contextPath.equals(request.getContextPath())) {
			return ServletMatchResult.UNMATCHED;
		}
		boolean matched = request.getRequestURI().startsWith(urlPrefix);
		ServletMatchResult result = ServletMatchResult.matched();
		return result;
	}

	private String getPrefix(String urlPattern) {
		return StringUtils.trimEnd(urlPattern, '*');
	}

	@Override
	public String toString() {
		return String.format("[context=%s;urlPattern=%s]", contextPath, urlPattern);
	}

	HttpHandlerRegistration getHandlerRegistration() {
		return HttpHandlerRegistration.builder().contextPath(contextPath).urlPattern(urlPattern).build();
	}
}
