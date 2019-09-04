package org.helium.http.ws.test;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.jaxws.JaxwsHandler;
import org.glassfish.grizzly.servlet.WebappContext;
import org.helium.http.test.HttpRootSampleServlet;

import javax.servlet.*;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.IOException;
import java.net.URL;
import java.util.EnumSet;

/**
 * Created by Coral on 8/19/15.
 */
public class ServletMain {
	public static void main(String[] args) throws Exception {
		HttpServer httpServer = new HttpServer();
		NetworkListener networkListener = new NetworkListener("jaxws-listener", "0.0.0.0", 8087);

		HttpHandler httpHandler = new JaxwsHandler(new AddService());

		HttpRootSampleServlet servlet = new HttpRootSampleServlet();
		WebappContext wac = new WebappContext("test", "");
		FilterRegistration fr = wac.addFilter("root", new Filter() {
			@Override
			public void init(FilterConfig filterConfig) throws ServletException {

			}

			@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
				response.getOutputStream().write("Filter".getBytes());
				response.getOutputStream().close();
			}

			@Override
			public void destroy() {

			}
		});
		fr.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
		//ServletRegistration sr = wac.addServlet("servlet1", servlet);
		// sr.addMapping("/google.go");
		wac.deploy(httpServer);


		// ServletHandler handler = new ServletHandler();

		//httpServer.getServerConfiguration().addHttpHandler(httpHandler, "/add");
		// httpServer.getServerConfiguration().addHttpHandler(new ServletHandler(servlet));
		httpServer.addListener(networkListener);
		httpServer.start();

		// Thread.sleep(10000);

//		wac.undeploy();
//		wac.addServlet("servlet2", servlet);
//		wac.deploy(httpServer);


		try {
			Service ws = Service.create(
					new URL("http://127.0.0.1:8089/add?wsdl"),
					new QName("http://test.ws.http.helium.org/", "AddServiceService"));

			AddServiceInterface service = ws.getPort(AddServiceInterface.class);
			System.out.printf("add=%d", service.add(100, 200));
			System.out.printf("add=%d", service.add(100, 300));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

//		assertNotNull(calculatorService);
//
//		CalculatorWs calculator = calculatorService.getPort(CalculatorWs.class);


		while (true) {
			Thread.sleep(100);
		}

	}
}
