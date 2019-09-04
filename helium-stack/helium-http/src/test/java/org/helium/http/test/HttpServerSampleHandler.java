package org.helium.http.test;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpHandlerRegistration;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

/**
 * Created by Coral on 7/23/15.
 */
public class HttpServerSampleHandler extends HttpHandler {
	private String context;
	private String pattern;

	public HttpServerSampleHandler(String context, String pattern) {
		this.context = context;
		this.pattern = pattern;
	}

	public HttpHandlerRegistration getRegistration() {
		return HttpHandlerRegistration.bulder().contextPath(context).urlPattern(pattern).build();
	}

	@Override
	public void service(Request request, Response response) throws Exception {
		String msg = String.format("context: %s; urlPattern: %s", context, pattern);
		response.getOutputStream().write(msg.getBytes());
		response.getOutputStream().close();
	}
}
