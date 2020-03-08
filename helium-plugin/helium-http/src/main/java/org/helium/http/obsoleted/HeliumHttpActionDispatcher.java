package org.helium.http.obsoleted;//package helium.http;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//public class HeliumHttpActionDispatcher extends HeliumHttpServlet {
//
//	private Action action;
//
//	private static final long serialVersionUID = -2144403288901322958L;
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(HeliumHttpActionDispatcher.class);
//
//	@Override
//	public void startup() {
//
//	}
//
//	@Override
//	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
//			IOException {
//		LOGGER.info("Invoke http action service.");
//		HeliumHttpAction httpAction = action.getClassObject(this.getClassLoader());
//		httpAction.service(action, request, response);
//	}
//
//	@Override
//	public void shutdown() {
//
//	}
//
//}
