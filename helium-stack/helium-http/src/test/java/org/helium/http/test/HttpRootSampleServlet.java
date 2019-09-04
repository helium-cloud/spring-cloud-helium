package org.helium.http.test;

import org.helium.framework.annotations.FieldSetter;
import org.helium.framework.annotations.ServiceSetter;
import org.helium.framework.annotations.ServletImplementation;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.http.servlet.HttpMappings;
import org.helium.http.servlet.HttpServlet;
import org.helium.http.servlet.HttpServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Coral on 7/23/15.
 */
@ServletImplementation(id = "sample:HttpSampleServlet")
@HttpMappings(contextPath = "/", urlPattern = "/*")
public class HttpRootSampleServlet extends HttpServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpRootSampleServlet.class);

	@FieldSetter("${HELLO}")
	private String hello = "hello";

	@ServiceSetter
	private ConfigProvider configProvider;

	@Override
	public void process(HttpServletContext ctx) throws Exception {
		LOGGER.warn("HttpSampleServlet.process, {}", ctx.getRequest().getRequestURL());
		ctx.getResponse().getOutputStream().print("Hello From Root:" + hello);
		// ctx.getResponse().getOutputStream().print(configProvider.loadText("bootstrap.xml"));
		ctx.getResponse().getOutputStream().close();
	}
}
