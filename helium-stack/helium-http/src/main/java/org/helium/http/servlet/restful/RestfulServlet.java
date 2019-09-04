package org.helium.http.servlet.restful;

import org.helium.http.servlet.HttpServlet;
import org.helium.http.servlet.HttpServletContext;

/**
 * 实现Restful风格的Servlet
 * Created by Coral on 1/12/17.
 */
public abstract class RestfulServlet extends HttpServlet {
	/**
	 * 处理GET
	 * @param ctx
	 */
	protected abstract void doGet(RestfulContext ctx) throws Exception;

	/**
	 * 处理PUT
	 * @param ctx
	 */
	protected abstract void doPut(RestfulContext ctx) throws Exception;

	/**
	 * 处理DELETE
	 * @param ctx
	 */
	protected abstract void doDelete(RestfulContext ctx) throws Exception;

	/**
	 * 处理POST方法
	 * @param ctx
	 */
	protected abstract void doPost(RestfulContext ctx) throws Exception;

	@Override
	public void process(HttpServletContext ctx) throws Exception {
		RestfulContext c2 = new RestfulContext(ctx);
		switch (c2.getRestfulMethod()) {
			case GET:
				doGet(c2);
				break;
			case POST:
				doPost(c2);
				break;
			case PUT:
				doPut(c2);
				break;
			case DELETE:
				doDelete(c2);
				break;
		}
	}
}
