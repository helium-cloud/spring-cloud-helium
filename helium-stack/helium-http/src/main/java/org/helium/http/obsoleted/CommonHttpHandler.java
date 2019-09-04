//package org.helium.http.servlet.extension.spi;
//
//import Action;
//import org.glassfish.grizzly.http.server.HttpHandler;
//import org.glassfish.grizzly.http.server.HttpHandlerRegistration;
//import org.glassfish.grizzly.http.server.Request;
//import org.glassfish.grizzly.http.server.Response;
//import org.glassfish.grizzly.http.util.Header;
//import org.helium.framework.module.ModuleChain;
//import org.helium.framework.module.ModuleState;
//import org.helium.http.servlet.HttpModule;
//import org.helium.http.servlet.HttpServletContext;
//import org.helium.http.servlet.spi.HttpUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// *
// * Created by Coral on 7/23/15.
// */
//class CommonHttpHandler extends HttpHandler {
//	private static final Logger LOGGER = LoggerFactory.getLogger(CommonHttpHandler.class);
//
//	private String contextPath;
//	private ModuleChain chain;
//	private HttpHandlerRegistration registration;
//	private ServletHandler servlet;
//
//	public CommonHttpHandler(String contextPath) {
//		this.contextPath = contextPath;
//		this.chain = new ModuleChain();
//		LOGGER.info("create CommonHttpHandler for context:{}", contextPath);
//	}
//
//	public String getContextPath() {
//		return contextPath;
//	}
//
//	/**
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
//	@Override
//	public void service(Request request, Response response) throws Exception {
//		LOGGER.info("CommonHttpHandler.service: context={} request={}", contextPath, request.getRequestURI());
//		try {
//			this.processCommonHandler(request, response, new Action<HttpServletContext>() {
//				@Override
//				public void run(HttpServletContext ctx) {
//					if (!ctx.isTerminated()) {
//						if (servlet != null) {
//							try {
//								servlet.service(request, response);
//							} catch (Exception e) {
//								ctx.sendError(500);
//							}
//						} else {
//							ctx.sendError(404);
//						}
//					}
//				}
//			});
//		} catch (Exception ex) {
//			LOGGER.error("HttpHandler.service() error", ex);
//			if (!response.isCommitted()) {
//				response.sendError(500);
//			}
//		}
//	}
//
//	/**
//	 * 由普通的ServletHandler调用
//	 * @param request
//	 * @param response
//	 * @param cb
//	 */
//	void processCommonHandler(Request request, Response response, Action<HttpServletContext> cb) {
//		try {
//			HttpServletContext ctx = createContext(request, response);
//			chain.innerProcess(ctx, new Action<ModuleState>() {
//				@Override
//				public void run(ModuleState state) {
//					LOGGER.info("processCommonHandler finised: {}", state);
//					if (!state.isTerminated()) {
//						cb.run(ctx);
//					}
//				}
//			});
//		} catch (Exception ex) {
//			if (!response.isCommitted()) {
//				HttpUtils.sendErrorPage(response, ex);
//			}
//		}
//	}
//
//	public void addModule(HttpModule module) {
//		chain.addModule(module);
//	}
//
//	public HttpServletContext createContext(Request request, Response response) throws Exception {
//		// TODO: 校验HTTP
////			if (servlet.isSSL() && !"https".equalsIgnoreCase(scheme)) {
////				logger.info("https 404");
////				HtmlHelper.sendErrorPage(request, response, response.getErrorPageGenerator(), 404, "", "", null);
////				return;
////			}
//		// 为用户初始化Service Context 用于获取全局Beans对象
//		// ThreadContext.getCurrent().putNamedContext(ThreadContextName.SESSION_CONTEXT, NullContext.getInstance());
//
//		// HttpServletCounterCategory servletCounter = servlet.getServletCounter();
//
////			SmartCounter visit = servletCounter.getVisit();
////			SmartCounter counter = servletCounter.getProcess();
////			Stopwatch servletWatch = counter.begin();
//		String scheme = request.getScheme();
//
//		ServletRequestImpl servletRequest = ServletRequestImpl.create();
//		ServletResponseImpl servletResponse = ServletResponseImpl.create();
//
//		servletRequest.init(request, servletResponse);
//		servletResponse.initialize(response, servletRequest);
//		servletRequest.initSession();
//		servletResponse.addHeader(Header.Server.toString(), "Helium/2.0.1");
//		HttpServletContext ctx = new HttpServletContext(servletRequest, servletResponse);
//		return ctx;
//	}
//
//	public void setServlet(ServletHandler servlet) {
//		this.servlet = servlet;
//	}
//}
