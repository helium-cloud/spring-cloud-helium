package org.helium.sample.bootstrap;


import org.helium.framework.annotations.FieldSetter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Coral on 5/11/15.
 */
public class SampleHttpServlet extends HttpServlet {
	@FieldSetter("sample:SampleBean")
	private SampleBean sampleBean;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getOutputStream().println(sampleBean.hello("foo"));
	}
}
