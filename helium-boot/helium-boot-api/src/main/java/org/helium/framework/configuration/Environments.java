package org.helium.framework.configuration;

import org.helium.util.CollectionUtils;
import org.helium.util.ServiceEnviornment;
import org.helium.util.StringUtils;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.helium.framework.entitys.KeyValueNode;
import org.helium.framework.utils.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.time.temporal.ChronoField.*;

/**
 * 本机约定的环境变量名
 * Created by Coral on 5/15/15.
 */
public final class Environments {
	private static final int AUTO_PORT_START = 9000;
	private static final String AUTO_PORT_MACRO = "${AUTO_PORT}";
	private static final String AUTO_GUID = "${AUTO_GUID}";
	private static Marker MARKER = MarkerFactory.getMarker("ENV");

	private static final DateTimeFormatter VERSION_FORMATTER = new DateTimeFormatterBuilder()
			.parseCaseInsensitive()
			.appendValue(MONTH_OF_YEAR, 2)
			.appendValue(DAY_OF_MONTH, 2)
			.appendValue(HOUR_OF_DAY, 2)
			.appendValue(MINUTE_OF_HOUR, 2)
			.toFormatter();

	public static final String FRAMEWORK_VERSION = "2.1.8";
	public static final String RUNTIME_VERSION = FRAMEWORK_VERSION + "." + LocalDateTime.now().format(VERSION_FORMATTER);

	public static final String SERVER_ID = "SERVER_ID";
	public static final String LOCAL_IP = "LOCAL_IP";
	public static final String USER_NAME = "USER_NAME";

	private static final Logger LOGGER = LoggerFactory.getLogger(Environments.class);
	private static final Map<String, String> vars = new HashMap<>();

	private static int autoPort = AUTO_PORT_START + (Environments.getPid() % 500);

	/**
	 * 加载系统配置
	 */
	public static void loadSystemVariables() {
		vars.put(Environments.USER_NAME, ConfigUtils.getUserName());

		boolean detectIp = true;
		for (Entry<String, String> e: System.getenv().entrySet()) {
			vars.put(e.getKey(), e.getValue());
			if ("LOCAL_IP".equalsIgnoreCase(e.getKey()) || "PRIVATE_IP".equalsIgnoreCase(e.getKey()) ||
					"PUBLIC_IP".equalsIgnoreCase(e.getKey())) {
				detectIp = false;
			}
		}

		for (Entry<String, String> e: vars.entrySet()) {
			LOGGER.info(MARKER, "set Env {}={}", e.getKey(), e.getValue());
		}

		if (detectIp) {
			vars.put(Environments.LOCAL_IP, ConfigUtils.getLocalIp());
		}
	}

	public static void loadVariables(List<KeyValueNode> nodes) {
		if (nodes == null) {
			return;
		}
		for (KeyValueNode node : nodes) {
			if (!StringUtils.isNullOrEmpty(node.getKey()) && !StringUtils.isNullOrEmpty(node.getValue())) {
				vars.put(node.getKey(), node.getValue());
			}
		}
		int retry = 0;
		boolean evaluated = false;
		String var = "";
		while (!evaluated) {
			evaluated = true;
			for (Entry<String, String> e: CollectionUtils.cloneEntrys(vars)) {
				if (e.getValue().contains("${")) {
					var = e.getValue();
					String v2 = applyConfigVariable(e.getValue());
					vars.put(e.getKey(), v2);
					evaluated = false;
				}
			}
			retry++;
			if (retry > 10) {
				throw new IllegalArgumentException("loadVariables can't resolve:" + var);
			}
		}
	}

	/**
	 * 使用环境变量替换掉配置字符串
	 * @param text
	 * @return
	 */
	public static String applyConfigVariable(String text) {
		for (Entry<String, String> e: vars.entrySet()) {
			text = text.replace("${" + e.getKey()+ "}", e.getValue());
		}
		return text;
	}

	/**
	 * 使用环境变量替换掉字符串
	 * @param text
	 * @return
	 */
	public static String applyConfigText(String path, String text) {
		if (vars.size() == 0) {
			return text;
		}
		text = applyAutoPort(text);
		try {
			Configuration c = new Configuration(freemarker.template.Configuration.VERSION_2_3_22);
			StringTemplateLoader loader = new StringTemplateLoader();
			loader.putTemplate("T", text);
			c.setTemplateLoader(loader);
			Template template = c.getTemplate("T");
			StringWriter writer = new StringWriter();
			template.process(vars, writer);
			return writer.toString();
		} catch (Exception ex) {
			throw new IllegalArgumentException("freemarker process failed:" + path, ex);
		}
	}

	public static String applyAutoPort(String text) {
		BufferedReader reader = new BufferedReader(new StringReader(text));
		StringBuilder str = new StringBuilder();
		while (true) {
			String line = null;
			try {
				line = reader.readLine();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			if (line == null) {
				break;
			}
			if (line.contains(AUTO_PORT_MACRO)) {
				while (true) {
					autoPort++;
					try {
						ServerSocket socket = new ServerSocket(autoPort);
						socket.close();
						LOGGER.info(MARKER, "AUTO_PORT selected port={}", autoPort);
						break;
					} catch (BindException ex) {
						autoPort += (Environments.getPid() & 0xff);
						LOGGER.info(MARKER, "AUTO_PORT detected port={} in use", autoPort);
					} catch (Exception ex) {
						LOGGER.error(MARKER, "AUTO_PORT detected failed {}", ex);
					}
				}
				line = line.replace(AUTO_PORT_MACRO, Integer.toString(autoPort));
			}
			str.append(line);
			str.append("\r\n");
		}
		return str.toString();
	}

	/**
	 * 得到一个环境变量
	 * @param key
	 * @return
	 */
	public static String getVar(String key) {
		return vars.get(key);
	}

	/**
	 * 返回所有的环境变量
	 * @return
	 */
	static Map<String, String> getVars() {
		return Collections.unmodifiableMap(vars);
	}

	/**
	 *
	 * @return
	 */
	public static int getPid() {
		return ServiceEnviornment.getPid();
	}

//
//	public static void main(String[] args) {
//		System.out.println(RUNTIME_VERSION);
//	}
}
