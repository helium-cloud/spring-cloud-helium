package org.helium.framework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by Coral on 8/6/15.
 */
public class ConfigUtils {
	public static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtils.class);
	private static final String TEST_DOMAIN = "www.baidu.com";
	private static final String LOCALHOST = "127.0.0.1";
	private static final int TEST_TIMEOUT = 2 * 1000;
	private static String localIp = null;

	public static String getLocalIp() {
		/*if (localIp != null) {
			return localIp;
		}

		final Outer<String> address = new Outer<>();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Socket s = new Socket(TEST_DOMAIN, 80);
					String ip = s.getLocalAddress().getHostAddress();
					s.close();
					address.setValue(ip);
				} catch (Exception ex) {
					LOGGER.error("Test LOCAL_IP failed: {}", ex);
					address.setValue(LOCALHOST);
				}
			}
		});
		thread.start();

		try {
			thread.join(TEST_TIMEOUT);
		} catch (InterruptedException ex) {
		} catch (Exception ex) {
			LOGGER.error("Test LOCAL_IP failed: {}", ex);
		}

		if (localIp == null) {
			localIp = address.value() != null ? address.value() : LOCALHOST;
			LOGGER.warn("set LOCAL_IP={}", localIp);
		}*/

		return LOCALHOST;
	}

	public static String getUserName() {
		return System.getProperty("user.name");
	}

	public static String getHostName() {
		return getUserName() + "_" + getLocalIp().replace(".", "_");
	}

	public static void main(String[] args) throws Exception {
		System.out.println(getLocalIp());
		System.out.println();
		System.out.println("COMPUTERNAME environment variable: " + System.getenv("COMPUTERNAME"));
		System.out.println(getHostName());

		InetAddress ip;
		String hostname;
		try {
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostName();
			System.out.println("Your current IP address : " + ip);
			System.out.println("Your current Hostname : " + hostname);

		} catch (UnknownHostException e) {

			e.printStackTrace();
		}
		Properties p = System.getProperties();
		Enumeration keys = p.keys();
		while (keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			String value = (String)p.get(key);
			System.out.println(key + ": " + value);
		}
	}
}
