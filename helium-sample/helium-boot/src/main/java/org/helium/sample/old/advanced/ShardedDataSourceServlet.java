//package org.helium.sample.old.advanced;
//
//import org.helium.framework.annotations.ServiceSetter;
//import org.helium.framework.annotations.ServletImplementation;
//import org.helium.http.servlet.HttpMappings;
//import org.helium.http.servlet.HttpServlet;
//import org.helium.http.servlet.HttpServletContext;
//import org.helium.logging.spi.LogUtils;
//import org.helium.sample.old.quickstart.SampleService;
//import org.helium.sample.old.quickstart.SampleUser;
//
///**
// * Created by Coral on 7/13/17.
// */
//@ServletImplementation(id = "quickstart:ShardedDataSourceServlet")
//@HttpMappings(contextPath = "/quickstart", urlPattern = "/sharding")
//public class ShardedDataSourceServlet extends HttpServlet {
//	/**
//	 * SampleService
//	 */
//	@ServiceSetter
//	private SampleService sampleService;
//
//	@Override
//	public void process(HttpServletContext ctx) throws Exception {
//		try {
//			int id = Integer.parseInt(ctx.getRequest().getParameter("id"));
//			SampleUser user = sampleService.getUser(id);
//			ctx.getResponse().getOutputStream().print(user.toJsonObject().toString());
//		} catch (Exception e) {
//			ctx.getResponse().getOutputStream().println(LogUtils.formatError(e));
//		}
//		ctx.getResponse().getOutputStream().close();
//	}
//}
