package org.helium.http.servlet.restful;

import org.helium.http.servlet.HttpServletContext;
import org.helium.http.servlet.HttpServletException;
import org.helium.http.utils.UrlPatternPath;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gao Lei on 1/17/17.
 */
public class RestfulContext extends HttpServletContext {
	private RestfulMethod method;
	private Map<String, String> pathParameters = new HashMap<>();
	
	public RestfulMethod getRestfulMethod() {
		return method;
	}
	
	public RestfulContext(HttpServletContext ctx) {
		super(ctx.getRequest(), ctx.getResponse());
		this.setContextPath(ctx.getContextPath());
		this.setPattern(ctx.getPattern());

		method = RestfulMethod.fromName(ctx.getRequest().getMethod());
		if (method == null) {
			throw new HttpServletException();
		}

		String requestUri = ctx.getRequest().getRequestURI();
		String relativePath = requestUri.substring(getContextPath().length());
		
		if (getPattern() instanceof UrlPatternPath) {
			UrlPatternPath path = (UrlPatternPath)getPattern(); 
			path.matchPath(relativePath, (k, v) -> pathParameters.put(k, v));
		}
	}

	public String getPathParameter(String k) {
		return pathParameters.get(k);
	}
}
