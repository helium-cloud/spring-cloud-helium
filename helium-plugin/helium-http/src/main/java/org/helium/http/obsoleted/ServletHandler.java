package org.helium.http.obsoleted;//package org.helium.http.servlet.extension.spi;
//
//import org.helium.util.Action;
//import org.glassfish.grizzly.http.server.HttpHandler;
//import org.glassfish.grizzly.http.server.Request;
//import org.glassfish.grizzly.http.server.Response;
//import org.helium.framework.BeanContext;
//import org.helium.framework.module.ModuleChain;
//import org.helium.framework.module.ModuleState;
//import org.helium.framework.spi.BeanInstance;
//import org.helium.http.servlet.HttpServlet;
//import org.helium.http.servlet.HttpServletContext;
//import org.helium.http.servlet.spi.HttpServletWrapper;
//import org.helium.http.servlet.spi.HttpUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * @author Will.jingmiao
// * @version 创建时间：2014年9月24日 类说明
// */
//public class ServletHandler extends HttpHandler {
//	private Logger logger;
//	private BeanContext beanContext;
//	private CommonHttpHandler commonHandler;
//	private ModuleChain moduleChain;
//	private HttpServlet servlet;
//
//	public ServletHandler(BeanContext context, CommonHttpHandler commonHandler) {
//		this.beanContext = context;
//
//		if (context.getBean() instanceof HttpServlet) {
//			this.servlet = (HttpServlet)context.getBean();
//		} else {
//			this.servlet = new HttpServletWrapper((javax.servlet.http.HttpServlet)context.getBean());
//		}
//
//		this.commonHandler = commonHandler;
//		this.moduleChain = ((BeanInstance)beanContext).getModuleChain();
//		this.logger = LoggerFactory.getLogger(servlet.getClass());
//	}
//
//	@Override
//	public void service(Request request, Response response) throws Exception {
//		try {
//			commonHandler.processCommonHandler(request, response, new Action<HttpServletContext>() {
//				@Override
//				public void run(HttpServletContext ctx) {
//					if (!ctx.isTerminated()) {
//						processChain(ctx);
//					}
//				}
//			});
//		} catch (Exception ex) {
//			logger.error("HttpHandler.service() error", ex);
//			if (!response.isCommitted()) {
//				HttpUtils.sendErrorPage(response, ex);
//			}
//		}
//	}
//
//	private void processChain(HttpServletContext ctx) {
//		try {
//			moduleChain.innerProcess(ctx, new Action<ModuleState>() {
//				@Override
//				public void run(ModuleState a) {
//					if (!a.isTerminated()) {
//						processHttpServlet(ctx);
//					}
//				}
//			});
//		} catch (Exception ex) {
//			ctx.sendError(ex);
//		}
//	}
//
//	private void processHttpServlet(HttpServletContext ctx) {
//		try {
//			servlet.process(ctx);
////			visit.increase();
//		} catch (Exception ex) {
////			servletWatch.fail(e);
////			servletCounter.getErrors().increase();
//			ctx.sendError(ex);
//			logger.error("processHttpServlet failed: {}", ex);
//			HttpServletRequest request = ctx.getRequest();
//			HttpServletResponse response = ctx.getResponse();
//			if (!response.isCommitted()) {
//				try {
//					response.getOutputStream().write(ex.toString().getBytes());
//					response.sendError(500);
//				} catch (IOException e) {
//					logger.error("send 500 failed:{}", e);
//				}
//			}
//		}
//	}
//}
//
///* 老版本代码
//
////	public void setImsPath(String imsPath) {
////		this.imsPath = imsPath;
////
////		int indexOf = imsPath.indexOf("*");
////		isMatching = indexOf > 0;
////		if (isMatching) {
////			startMatch = imsPath.substring(0, indexOf);
////			endMatch = imsPath.substring(indexOf + 1);
////		}
////	}
//		SmartCounter workerCounter = counterCategoy.getWorkerCounter();
////		Stopwatch watch = workerCounter.begin();
//		String scheme = request.getScheme();
//		try {
//			final ServletRequestImpl servletRequest = ServletRequestImpl.create();
//			final ServletResponseImpl servletResponse = ServletResponseImpl.create();
//
//			servletRequest.init(request, servletResponse);
//			servletResponse.initialize(response, servletRequest);
//			servletRequest.initSession();
//			servletResponse.addHeader(Header.Server.toString(), "Helium/2.0.1");
//
//			final HttpServletContext ctx = new HttpServletContext(servletRequest, servletResponse);
//
//			globalChain.processModuleChain(ctx, new Action<ModuleState>() {
//				@Override
//				public void run(ModuleState a) {
//					if (a.isTerminated()) {
//						return;
//					}
//
//					process2(ctx);
//				}
//			});
////			servletWatch.end();
////			watch.end();
//		} catch (Throwable ex) {
////			LOGGER.info("service exception:", ex);
////			watch.fail(ex);
//			if (!response.isCommitted()) {
//				try {
//					HtmlHelper.setErrorAndSendErrorPage(response.getRequest(), response, response.getErrorPageGenerator(), 500, "Internal Error",
//							"Internal Error", ex);
//				} catch (IOException e1) {
//				}
//			}
//		}
// */