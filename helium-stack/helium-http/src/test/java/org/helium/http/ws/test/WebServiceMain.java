package org.helium.http.ws.test;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.jaxws.JaxwsHandler;

import java.io.IOException;

/**
 * Created by Coral on 8/18/15.
 */
public class WebServiceMain {
	public static void main(String[] args) throws IOException, InterruptedException {
		HttpServer httpServer = new HttpServer();
		NetworkListener networkListener = new NetworkListener("jaxws-listener", "0.0.0.0", 8088);

		HttpHandler httpHandler = new JaxwsHandler(new AddService());
		httpServer.getServerConfiguration().addHttpHandler(httpHandler, "/add");
		httpServer.addListener(networkListener);

		httpServer.start();


		while (true) {
			Thread.sleep(100);
		}
	}
}
