//package org.helium.sample.old.quickstart;
//
//
//import org.helium.framework.annotations.FieldSetter;
//import org.helium.framework.annotations.ServletImplementation;
//import org.helium.http.servlet.HttpMappings;
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
//@ServletImplementation(id = "quickstart:SampleServlet1")
//@HttpMappings(contextPath = "/quickstart", urlPattern = "/sample1")
//public class SampleHttpServlet1 extends HttpServlet {
//	/**
//	 * 注入
//	 */
//	@FieldSetter("${USER_NAME}")
//	private String name;
//
//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		resp.getOutputStream().println("Hello: " + name);
//		resp.getOutputStream().close();
//	}
//}
