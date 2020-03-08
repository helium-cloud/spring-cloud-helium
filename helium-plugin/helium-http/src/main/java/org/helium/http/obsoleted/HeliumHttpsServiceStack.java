package org.helium.http.obsoleted;//package org.helium.http.server;
//
//import com.feinno.endpoints.HttpsEndpoint;
//import org.helium.util.TlsHelper;
//import org.glassfish.grizzly.http.servlet.NetworkListener;
//import org.glassfish.grizzly.ssl.SSLContextConfigurator;
//import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * @author Will.jingmiao
// * @version 创建时间：2014年10月16日 类说明
// */
//public class HeliumHttpsServiceStack extends HeliumHttpServiceStack {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(HeliumHttpsServiceStack.class);
//
//	@Override
//	public void registerServiceBean(ServiceConfiguration config, ServiceBean bean) {
//		try {
//			initHttpConnector();
//		} catch (Exception e) {
//			LOGGER.error("InitHttpConnector:", e);
//		}
//		registerBean(config, bean, true);
//	}
//
//	@Override
//	public void startup() throws Exception {
//		initHttpConnector();
//
//		if (endpoint instanceof HttpsEndpoint) {
//			HttpsEndpoint te = (HttpsEndpoint) endpoint;
//
//			LOGGER.info("HttpsProvider start in:" + te);
//			NetworkListener listener = new NetworkListener(te.getProtocol(), NetworkListener.DEFAULT_NETWORK_HOST, te.getPort());
//
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
//			LOGGER.error("Https Listener Port {} Ok!", ((HttpsEndpoint) endpoint).getPort());
//			connector.addListener(listener);
//
//		} else {
//			throw new IllegalArgumentException("Transport Protocol must Https!");
//		}
//
//		if (!connector.isStart()) {
//			connector.start();
//			LOGGER.info("HttpsProvider started " + endpoint);
//		}
//	}
//
//}
