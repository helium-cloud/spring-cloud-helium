package org.helium.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * IP方面的工具类
 * <p>
 * 目前仅有端口探测，后续将陆续增加
 * 
 * @author Lv.Mingwei
 * 
 */
public class NetworkUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(NetworkUtils.class);

	/**
	 * 探测当前端口是否使用中
	 * 
	 * @param port
	 * @return
	 */
	public static boolean isPortListening(int port) {
		try {
			ServerSocket s = new ServerSocket(port);
			s.close();
			return false;
		} catch (IOException e) {
			return true;
		}
	}

	/**
	 * 探测获取本机的IP
	 * @return
	 */
	public static String getLocalIp() {
		try {
			Socket s = new Socket(TEST_DOMAIN, 80);
			String ip = s.getLocalAddress().getHostAddress();
			s.close();
			return ip;
		} catch (Exception ex) {
			LOGGER.error("test local ip failed:{}", ex);
			return "127.0.0.1";
		}
	}

	public static final String TEST_DOMAIN = "www.baidu.com";
}
