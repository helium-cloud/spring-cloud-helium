package org.helium.http.servlet;

import org.helium.util.StringUtils;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.helium.framework.BeanContext;
import org.helium.framework.route.ServerUrl;
import org.helium.framework.servlet.ServletDescriptor;
import org.helium.framework.servlet.ServletStack;
import org.helium.http.servlet.spi.HttpServerImpl;
import org.helium.http.servlet.spi.HttpsKeyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 应当如何设计
 * 1. contextPath是必须的
 * 2. 仅设计一个HttpHandler
 * Created by Lei Gao on 8/24/15.
 */
public class HttpServletStack implements ServletStack {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpServletStack.class);

	private String id;
	private String host;
	private int port = 0;
	private int sslPort = 0;
	private String keyStoreFilePath;
	private String keyStorePassword;
	private String keyPassword;
	private String keyStoreType = HttpsKeyStore.KEKSTORE_JCEKS;


	private int workerSize = 0;

	private HttpServerImpl server;

	public HttpServletStack() {
		server = new HttpServerImpl();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public List<ServerUrl> getServerUrls() {
		List<ServerUrl> eps = new ArrayList<>();
		if (port != 0) {
			eps.add(ServerUrl.parse("http://" + host + ":" + port + ";protocol=http"));
		}
		if (sslPort != 0) {
			eps.add(ServerUrl.parse("https://" + host + ":" + sslPort + ";protocol=http"));
		}
		return eps;
	}

	@Override
	public boolean isSupportServlet(Object servlet) {
		return (servlet instanceof HttpServlet);
	}

	@Override
	public boolean isSupportModule(Object module) {
		return (module instanceof HttpModule);
	}

	@Override
	public ServletDescriptor getServletDescriptor() {
		return HttpServletDescriptor.INSTANCE;
	}

	@Override
	public void registerModule(BeanContext context) {
		server.registerModule(context);
	}

	@Override
	public void registerServlet(BeanContext context) {
		server.registerServlet(context);
	}

	@Override
	public void unregisterModule(BeanContext context) {
		server.unregisterModule(context);
	}

	@Override
	public void unregisterServlet(BeanContext context) {
		server.unregisterServlet(context);
	}

	public void registerWebService(Object wsObject, String wsPath) {
		server.registerWebService(wsObject, wsPath);
	}

	public boolean unregisterWebService(String wsPath) {
		return server.unregisterWebService(wsPath);
	}

	@Override
	public void start() throws Exception {
		if (StringUtils.isNullOrEmpty(host)) {
			host = NetworkListener.DEFAULT_NETWORK_HOST;
		}

		if(workerSize <= 0) {
			server.addHttpListener(host, port);
		}else{
			server.addHttpListener(host,port,workerSize);
		}


		if (sslPort != 0) {
			server.addHttpsListener(host, sslPort, keyStoreFilePath, keyStorePassword, keyPassword, keyStoreType);
			String path = null;
			String absolutePath = System.getProperty("user.dir")  + File.separatorChar + "config" + File.separatorChar + "crt" + File.separatorChar + keyStoreFilePath;
			if(!StringUtils.isNullOrEmpty(keyStoreFilePath) && new File(keyStoreFilePath).exists())

				path = keyStoreFilePath;
			else if(new File(absolutePath).exists()){
				path = absolutePath;
			}
			server.addHttpsListener(host, sslPort, path, keyStorePassword, keyPassword, keyStoreType);
			LOGGER.info("load ssl key file." + absolutePath);
		}
		LOGGER.info("Start {} service", this.getClass().getSimpleName());
		server.start();
		LOGGER.info("HttpServletStack started");
	}

	@Override
	public void stop() throws Exception {
		server.stop();
	}

	@Override
	public String getHost() {
		return host;
	}
}
