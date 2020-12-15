package org.helium.http.obsoleted;//package helium.http;
//
//import org.glassfish.grizzly.http.servlet.CLStaticHttpHandler;
//import org.glassfish.grizzly.http.servlet.Request;
//import org.glassfish.grizzly.http.servlet.Response;
//import org.glassfish.grizzly.http.servlet.util.HtmlHelper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * @author Will.jingmiao
// * @version 创建时间：2014年9月28日 类说明
// */
//public class UStaticHttpHandler extends CLStaticHttpHandler {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(UStaticHttpHandler.class);
//
//	public UStaticHttpHandler(ClassLoader classLoader, String[] docRoots) {
//		super(classLoader, docRoots);
//
//		this.setFileCacheEnabled(false);
//	}
//
//	@Override
//	public void service(Request request, Response response) throws Exception {
//		String requestURI = request.getRequestURI();
//		if(requestURI.startsWith("/beans")){
//			LOGGER.info("404");
//			HtmlHelper.sendErrorPage(request, response, response.getErrorPageGenerator(), 404, "", "", null);
//			return;
//		}
//		super.service(request, response);
//	}
//}
