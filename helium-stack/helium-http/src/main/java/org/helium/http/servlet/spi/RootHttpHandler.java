package org.helium.http.servlet.spi;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpHandlerRegistration;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

/**
 * Created by Coral on 8/25/15.
 */
public class RootHttpHandler extends HttpHandler {
	public HttpHandlerRegistration getRegistration() {
		return HttpHandlerRegistration.builder().contextPath("/").urlPattern("/*").build();
	}
	@Override
	public void service(Request request, Response response) throws Exception {
		response.sendError(404);
	}
}
