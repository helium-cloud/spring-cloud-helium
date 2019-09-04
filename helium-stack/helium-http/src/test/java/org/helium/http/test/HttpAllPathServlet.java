package org.helium.http.test;

import org.helium.framework.annotations.FieldSetter;
import org.helium.framework.annotations.ServletImplementation;
import org.helium.http.servlet.HttpMappings;
import org.helium.http.servlet.HttpServlet;
import org.helium.http.servlet.HttpServletContext;

/**
 * Created by Coral on 7/23/15.
 */
@ServletImplementation(id = "sample:HttpAllPathServlet")
@HttpMappings(contextPath = "/path", urlPattern = "/*")
public class HttpAllPathServlet extends HttpServlet {
	@FieldSetter("${HELLO}")
	private String hello;

	@Override
	public void process(HttpServletContext ctx) throws Exception {
		ctx.getResponse().getOutputStream().print("Path2 Hello:" + hello);
		ctx.getResponse().getOutputStream().close();
	}
}
