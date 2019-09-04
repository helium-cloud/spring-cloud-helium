package org.helium.http.test;


//import org.helium.framework.annotations.FixedExecutor;
import org.helium.framework.annotations.ServletImplementation;
import org.helium.http.servlet.HttpMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Coral on 5/11/15.
 */
@ServletImplementation(id = "sample:SampleHttpServlet")
@HttpMappings(contextPath = "/sample", urlPattern ="/hello.do")
//@FixedExecutor(name="test", size=10, limit=20)
public class SampleHttpServlet extends HttpServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(SampleHttpServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getOutputStream().write("Hello".getBytes());
		LOGGER.warn("HAHAHA ");
		resp.getOutputStream().flush();
		resp.getOutputStream().close();;
	}
}
