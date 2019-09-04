package org.helium.http.servlet.spi;

import org.helium.http.servlet.HttpServlet;
import org.helium.http.servlet.HttpServletContext;

/**
 * Created by Coral on 8/25/15.
 */
public class RootHttpServlet extends HttpServlet {
	@Override
	public void process(HttpServletContext ctx) throws Exception {
		ctx.sendError(404);
	}
}
