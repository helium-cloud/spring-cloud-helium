package org.helium.http.test.restful;

import org.helium.framework.annotations.ServletImplementation;
import org.helium.http.servlet.HttpMappings;
import org.helium.http.servlet.restful.RestfulContext;
import org.helium.http.servlet.restful.RestfulServlet;

/**
 * Created by Coral on 1/12/17.
 */
@HttpMappings(contextPath = "/restful", urlPattern = "/$UserId/$Tag/*")
@ServletImplementation(id = "test:TestRestfulServlet")
public class TestRestfulServlet extends RestfulServlet {
	@Override
	protected void doGet(RestfulContext ctx)  throws Exception {
		String userId = ctx.getPathParameter("$UserId");
		String tag = ctx.getPathParameter("$Tag");
		ctx.getResponse().getOutputStream().print("UserId:" + userId);
		ctx.getResponse().getOutputStream().print("Tag:" + tag);
	}

	@Override
	protected void doPut(RestfulContext ctx) {

	}

	@Override
	protected void doDelete(RestfulContext ctx) {

	}

	@Override
	protected void doPost(RestfulContext ctx) {

	}
}
