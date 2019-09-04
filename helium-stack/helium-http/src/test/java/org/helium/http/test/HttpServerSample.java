package org.helium.http.test;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.ServerConfiguration;

/**
 * Created by Coral on 7/23/15.
 */
public class HttpServerSample {

	public static void main(String[] args) throws Exception {
		HttpServer server = new HttpServer();
		ServerConfiguration configuration = server.getServerConfiguration();

//		StaticResourceHandler srh = new StaticResourceHandler("/foo", "webroot", HttpServerSample.class);
//		configuration.addHttpHandler(srh, "/foo", "/*");

	    HttpServerSampleHandler handler;
		handler = new HttpServerSampleHandler("", "/*");
		configuration.addHttpHandler(handler, handler.getRegistration());

		handler = new HttpServerSampleHandler("/foo", "/*");
		configuration.addHttpHandler(handler, handler.getRegistration());

		handler = new HttpServerSampleHandler("/foo", "/ACTION/*");
		configuration.addHttpHandler(handler, handler.getRegistration());

		// http://127.0.0.1:8081/foo/ACTION.post
		handler = new HttpServerSampleHandler("/foo", "/ACTION/post/*");
		configuration.addHttpHandler(handler, handler.getRegistration());

		NetworkListener listener = listener = new NetworkListener("Helium-2.0.3", "127.0.0.1", 8081);

		if (false) {
			// TODO enable SSL
//			SSLContextConfigurator contextConfig = new SSLContextConfigurator();
//			contextConfig.setSecurityProtocol("TLSV1.2");
//			contextConfig.setKeyStoreBytes(TlsHelper.getKeyBytesByName("keystore.jks"));
//			contextConfig.setKeyStorePass("123456");
//			contextConfig.setKeyPass("123456");
//			contextConfig.setKeyStoreType(TlsHelper.KEKSTORE_JCEKS);
//
//			SSLEngineConfigurator engineConfig = new SSLEngineConfigurator(contextConfig.createSSLContext(), false, false, false);
//
//			listener.setSSLEngineConfig(engineConfig);
//			listener.setSecure(true);listener.setAuthPassThroughEnabled(true);
		}
		server.addListener(listener);
		server.start();
		server.start();

		while (true) {
			Thread.sleep(100);
		}
	}
}
