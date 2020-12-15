package org.helium.http.servlet.spi;

import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.helium.http.servlet.HttpServletStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Lei Gao on 8/25/15.
 */
public class HttpsKeyStore {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpsKeyStore.class);

	private static final String PROTOCOL = "TLSv1.2";
	private static final String DEFAULT_SSL_KEY_STORE = "fetiononline.com.jks";
	private static final String DEFAULT_SSL_KEY_STORE_PASS = "secure";
	private static final String DEFAULT_SSL_KEY_PASS = "secure";

	public NetworkListener createListener(String host, int port, String keyStoreFilePath, String keyStorePassword, String keyPassword, String keyStoreType) {
		SSLContextConfigurator contextConfig = new SSLContextConfigurator();
		contextConfig.setSecurityProtocol(PROTOCOL);

		if (keyStoreFilePath != null && !keyStoreFilePath.isEmpty()) {
			contextConfig.setKeyStoreFile(keyStoreFilePath);
			contextConfig.setKeyStorePass(keyStorePassword);
			contextConfig.setKeyPass(keyPassword);
			contextConfig.setKeyStoreType(keyStoreType);
		} else {
			LOGGER.info("Load default ssl key store. -> {}", DEFAULT_SSL_KEY_STORE);
			contextConfig.setKeyStoreBytes(getKeyBytesByName(HttpServletStack.class.getClassLoader(), DEFAULT_SSL_KEY_STORE));
			contextConfig.setKeyStorePass(DEFAULT_SSL_KEY_STORE_PASS);
			contextConfig.setKeyPass(DEFAULT_SSL_KEY_PASS);
			contextConfig.setKeyStoreType(KEKSTORE_JCEKS);
		}

		SSLEngineConfigurator engineConfig = new SSLEngineConfigurator(contextConfig.createSSLContext(), false, false, false);

		NetworkListener listener = new NetworkListener("Helium https", host, port);
		listener.setSSLEngineConfig(engineConfig);
		listener.setSecure(true);
		listener.setAuthPassThroughEnabled(true);
		LOGGER.info("create https listener {}:{}", host, port);
		return listener;
	}

	// KS的Provider是SUN，在每个版本的JDK中都有
	public static final String KEKSTORE_JKS = "jks";

	// JCEKS的Provider是SUNJCE，1.4后我们都能够直接使用它
	public static final String KEKSTORE_JCEKS = "jceks";

	// PKCS#12是公钥加密标准，它规定了可包含所有私钥、公钥和证书
	public static final String KEKSTORE_PKCS12 = "pkcs12";

	// BKS来自BouncyCastle Provider，它使用的也是TripleDES来保护密钥库中的Key
	public static final String KEKSTORE_BKS = "bks";

	// UBER比较特别，当密码是通过命令行提供的时候，它只能跟keytool交互
	public static final String KEKSTORE_UBER = "uber";

	/**
	 * 根据Key名 获取包内秘钥流
	 *
	 * @param name
	 * @return
	 */
	public static InputStream getKeyStreamByName(ClassLoader classLoader, String name) {
		return classLoader.getResourceAsStream(name);
	}

	public static byte[] getKeyBytesByName(String name) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		return getKeyBytesByName(classLoader, name);
	}

	public static byte[] getKeyBytesByName(ClassLoader classLoader, String name) {
		InputStream stream = getKeyStreamByName(classLoader, name);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int b;
		try {
			while ((b = stream.read()) != -1) {
				out.write(b);
			}

			return out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException("Read key Failed .");
		}
	}
}
