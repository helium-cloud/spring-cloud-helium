//package org.helium.sample.old.quickstart;
//
//import org.helium.framework.annotations.ServiceSetter;
//import org.helium.framework.annotations.ServletImplementation;
//import org.helium.http.servlet.HttpMappings;
//import org.helium.logging.spi.LogUtils;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * HttpServlet例子
// * Created by Coral on 5/11/15.
// */
//@ServletImplementation(id = "quickstart:SampleServlet2")
//@HttpMappings(contextPath = "/quickstart", urlPattern = "/sample2")
//public class SampleHttpServlet2 extends HttpServlet {
//	/**
//	 * SampleService
//	 */
//	@ServiceSetter(timeout = 1000)
//	private SampleService sampleService;
//
//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		try {
//			int id = Integer.parseInt(req.getParameter("id"));
//			SampleUser user = sampleService.getUser(id);
//			resp.getOutputStream().print(user.toJsonObject().toString());
//		} catch (Exception e) {
//			resp.getOutputStream().println(LogUtils.formatError(e));
//		}
//		resp.getOutputStream().close();
//	}
//}
