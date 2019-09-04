package org.helium.util;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

/**
 * 
 * <b>描述: </b>获取IMS配置文件的工具类,该工具类在寻找一个配置文件的时候会执行以下步骤<br>
 * 1. 首先根据操作系统类型从对应的目录中取<br>
 * 2. 如果上一步没有取到，则从启动目录中取<br>
 * 3. 如果还是没有找到，从当前环境变量中取
 * <p>
 * <b>功能: </b>
 * <p>
 * <b>用法: </b>
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class ConfigUtils {

	/** Windows系统下HA配置文件所在的根路径 */
	private static final String CONFIG_ROOT_PATH_WINDOWS = "C:\\rcs\\";

	/** Linux系统下HA配置文件所在的根路径 */
	private static final String CONFIG_ROOT_PATH_LINUX = "/data/rcs/";

	/** 以json表示的环境变量 */
	private static JsonObject ENV_JSON = null;

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtils.class);

	// static {
	// try {
	// LOGGER.info("load env by {}", getConfig("env.conf").getAbsolutePath());
	// String content = FileUtil.read(getConfig("env.conf"));
	// JsonParser jsonParser = new JsonParser();
	// JsonElement elements = jsonParser.parse(content);
	// ENV_JSON = elements.getAsJsonObject();
	// } catch (Exception e) {
	// LOGGER.error(String.format("load env.conf failed ."), e);
	// }
	// }

	/**
	 * 获得当前操作系统下的配置文件所在的根目录
	 * 
	 * @return
	 */
	public static String getRootPath() {
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Win") || osName.startsWith("win")) {
			LOGGER.info("OS Name : {} , Root Path : {}", osName, CONFIG_ROOT_PATH_WINDOWS);
			return CONFIG_ROOT_PATH_WINDOWS;
		} else {
			LOGGER.info("OS Name : {} , Root Path : {}", osName, CONFIG_ROOT_PATH_LINUX);
			return CONFIG_ROOT_PATH_LINUX;
		}
	}

	/**
	 * 获取一个指定的配置文件<br>
	 * 1. 首先根据操作系统类型从对应的目录中取<br>
	 * 2. 如果上一步没有取到，则从启动目录中取<br>
	 * 3. 如果还是没有找到，从当前环境变量的位置中取
	 * 
	 * @param fileName
	 * @return
	 */
	public static File getConfig(String fileName) throws FileNotFoundException {

		// Step 1. 通过ClassLoader取
		URL configUrl = ConfigUtils.class.getClassLoader().getResource(fileName);
		if (configUrl != null) {
			// 如果在包中，可以通过这种方法取到
			return new File(configUrl.getFile());
		}

		// Step 2. 从环境上取
		File configFile = new File(fileName);
		if (configFile.exists()) {
			return configFile;
		}

		// Step 3. 从固定路径上取
		configFile = new File(ConfigUtils.getRootPath() + fileName);
		if (configFile.exists()) {
			return configFile;
		}

		// 最终还是没有取到，则报错
		throw new FileNotFoundException("Not Found " + fileName);
	}

	/**
	 * 获取一个指定的配置文件<br>
	 * 1. 首先根据操作系统类型从对应的目录中取<br>
	 * 2. 如果上一步没有取到，则从启动目录中取<br>
	 * 3. 如果还是没有找到，从当前环境变量的位置中取
	 * 
	 * @param fileName
	 * @return
	 */
	public static InputStream getConfigAsStream(String fileName) throws FileNotFoundException {
		return getConfigAsStream(fileName, ConfigUtils.class.getClassLoader());
	}

	/**
	 * 获取一个指定的配置文件<br>
	 * 1. 首先根据操作系统类型从对应的目录中取<br>
	 * 2. 如果上一步没有取到，则从启动目录中取<br>
	 * 3. 如果还是没有找到，从当前环境变量的位置中取
	 * 
	 * @param fileName
	 * @param loader
	 * @return
	 */
	public static InputStream getConfigAsStream(String fileName, ClassLoader loader) throws FileNotFoundException {
		// Step 1. 通过ClassLoader取
		InputStream inputStream = loader.getResourceAsStream(fileName);
		if (inputStream != null) {
			// 如果在包中，可以通过这种方法取到
			return inputStream;
		}

		// Step 2. 从环境上取
		File configFile = new File(fileName);
		if (configFile.exists()) {
			return new FileInputStream(configFile);
		}

		// Step 3. 从固定路径上取
		configFile = new File(ConfigUtils.getRootPath() + fileName);
		if (configFile.exists()) {
			return new FileInputStream(configFile);
		}

		// 最终还是没有取到，则报错
		throw new FileNotFoundException("Not Found " + fileName);
	}

	/**
	 * 获得HA的环境变量，该环境变量从env.con中取得
	 * 
	 * @param key
	 * @return
	 */
	public static String getHAEnv(String key) {
		return ENV_JSON.get(key).getAsString();
	}

}
