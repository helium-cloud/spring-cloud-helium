package org.helium.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author coral
 * @version 创建时间：2014年10月27日 类说明
 */
public class TlsHelper {
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
