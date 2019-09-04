package org.helium.http.test;

import org.helium.framework.annotations.ServletImplementation;
import org.helium.http.servlet.HttpMappings;
import org.helium.http.servlet.HttpServlet;
import org.helium.http.servlet.HttpServletContext;

/**
 * Created by Coral on 7/28/15.
 */
@ServletImplementation(id = "sample:HttpJson")
@HttpMappings(contextPath = "/sample", urlPattern="/install.go")
public class HttpBundleInstallServlet extends HttpServlet {
	@Override
	public void process(HttpServletContext ctx) throws Exception {
		String location = ctx.getRequest().getParameter("location");
	}
}
