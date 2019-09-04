/**
 * Created by Coral on 6/9/15.
 */
package org.helium.framework.spi;

import org.helium.threading.SystemPropertyUtil;
import org.helium.util.CollectionUtils;
import org.helium.util.StringUtils;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.configuration.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@ServiceImplementation
public class ConfigProviderImpl implements ConfigProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigProviderImpl.class);
	private static final Marker MARKER = MarkerFactory.getMarker("HELIUM");

	private String root;
	private List<String> paths;
	private String configPath = null;
	private String libPath = null;
	private String binPath = null;
	private String logsPath = null;

	public ConfigProviderImpl() {
		paths = new ArrayList<String>();

		root = System.getProperty("user.dir");
		// 添加classPath
		addClassPath(root);

		//
		// 尝试识别目录结构,如果在本目录没识别出来,然后在上级目录识别出来了,则将根目录设置为上级目录
		if (!recognizeRoot(root)) {
			String parentRoot = getParentPath(root);
			if (recognizeRoot(parentRoot)) {
				root = parentRoot;
			}
		}
		LOGGER.warn(MARKER, ">>> ROOT={}", root);
		addPath(root);

		if (!StringUtils.isNullOrEmpty(configPath)) {
			addPath(configPath);
		}
	}

	private void addClassPath(String basePath) {
		String classPath = System.getProperty("java.class.path");
		addClassPath(basePath, classPath);
	}

	private void addClassPath(String basePath, String classPath) {
		String[] paths = StringUtils.split(classPath, File.pathSeparator);
		for (String path : paths) {
			if (path.equals("") || path.equals(".") || path.equals(File.pathSeparator)) {
				continue;
			}
			String p2 = concatPath(basePath, path);
			File f2 = new File(p2);
			if (f2.isDirectory()) {
				addPath(p2);
			} else {
				addPath(f2.getParent());
			}
		}
		//
		// 暂不加入ClassLoader的地址
//		ClassLoader cl = Bootstrap.class.getClassLoader();
//		String path = ClassLoader.getSystemClassLoader().getResource("").getPath();
//		addPath(path);
	}

	private boolean recognizeRoot(String path) {
		LOGGER.info(">>> recognizing Root path={}", path);
		// root;
		// parentDirectory;
		File dir = new File(path);
		String result = null;

		File[] files = dir.listFiles();
		if (files == null) {
			return false;
		}

		String binPath = null;
		String libPath = null;
		String logsPath = null;
		String configPath = null;

		for (File f : files) {
			if (f.getName().equals("bin") && f.isDirectory()) {
				binPath = f.getPath();
				LOGGER.info(MARKER, ">>> bin={}", binPath);
			}
			if (f.getName().equals("lib") && f.isDirectory()) {
				libPath = f.getPath();
				LOGGER.info(MARKER, ">>> lib={}", libPath);
			}
			if (f.getName().equals("logs") && f.isDirectory()) {
				logsPath = f.getPath();
				LOGGER.info(MARKER, ">>> logs={}", logsPath);
			}
			if (f.getName().equals("config") && f.isDirectory()) {
				configPath = f.getPath();
				LOGGER.info(MARKER, ">>> config={}", configPath);
			}
		}

		if (!StringUtils.isNullOrEmpty(configPath) && !StringUtils.isNullOrEmpty(libPath)) {
			LOGGER.info(">>> ROOT={}", root);
			this.binPath = binPath;
			this.libPath = libPath;
			this.logsPath = logsPath;
			this.configPath = configPath;

			return true;
		} else {
			return false;
		}
	}

	public String getRoot() {
		return root;
	}

	public String getPidFile() {
		if (StringUtils.isNullOrEmpty(logsPath)) {
			return root + File.separator + "helium.pid";
		} else {
			return logsPath + File.separator + "helium.pid";
		}
	}

	public void setRoot(String path) {
		setRoot(path, false);
	}

	public void setRoot(String path, boolean onlyRoot) {
		root = path;
		if (!paths.contains(path)) {
			paths.add(path);
		}
		if (onlyRoot) {
			paths.clear();
			paths.add(root);
		}
	}

	/**
	 * 检查给出的路径是否是绝对路径
	 *
	 * @param path 要判断的路径
	 * @return 绝对路径返回 true 否则返回 false
	 */
	private boolean isAbsolutePath(String path) {
		return path.startsWith("/") || path.matches("^[a-zA-Z]:\\\\.*");
	}

	/**
	 * 增加一个配置目录
	 *
	 * @param path
	 */
	public void addPath(String path) {
		if (!isAbsolutePath(path)) {
			path = root + File.separator + path;
		}
		if (!paths.contains(path)) {
			paths.add(path);
			LOGGER.info(">>> addPath: {} \n", path);
		}
	}

	@Override
	public InputStream loadRaw(String file) {
		for (String path : paths) {
			String p2 = path + File.separator + file;
			File f = new File(p2);
			if (!f.exists()) {
				continue;
			}
			FileInputStream stream;
			try {
				stream = new FileInputStream(p2);
			} catch (IOException e) {
				throw new IllegalArgumentException("load file failed:" + p2, e);
			}
			return stream;
		}
		throw new IllegalArgumentException("can't find " + file + " in paths");
	}

//	@Override
//	public String loadRawText(String file) {
//		for (String path : paths) {
//			String p2 = path + File.separator + file;
//			File f = new File(p2);
//			if (!f.exists()) {
//				continue;
//			}
//			String txt;
//			try {
//				txt = new String(Files.readAllBytes(Paths.get(p2)));
//			} catch (IOException e) {
//				throw new IllegalArgumentException("load file failed:" + p2, e);
//			}
//			return txt;
//		}
//		throw new IllegalArgumentException("can't find " + file + " in paths");
//	}

	@Override
	public boolean hasFile(String file) {
		for (String path : paths) {
			String p2 = path + File.separator + file;
			File f = new File(p2);
			if (f.exists()) {
				return true;
			}
		}
		return false;
	}

	public static String getParentPath(String path) {
		if (path.endsWith(File.separator)) {
			path = path.substring(0, path.length() - 1);
		}
		int l = path.lastIndexOf(File.separator);
		if (l > 0) {
			return path.substring(0, l);
		} else {
			return File.pathSeparator;
		}
	}

	public static String concatPath(String parent, String path) {
		if (path.endsWith(File.separator)) {
			path = path.substring(0, path.length() - 1);
		}
		File f = new File(path);
		if (f.isAbsolute()) {
			return path;
		}
		if (path.equals("..")) {
			return getParentPath(parent);
		} else {
			while (path.startsWith("../")) {
				parent = getParentPath(parent);
				path = path.substring(3, path.length());
			}
			return parent + File.separator + path;
		}
	}

	public String findJarFile(String pattern) {
		if (!pattern.endsWith("~.jar")) {
			if (isAbsolutePath(pattern)) {
				return pattern;
			} else {
				String s = findFiles(pattern);
				if (s == null) {
					throw new IllegalArgumentException("jar not found with:" + pattern);
				}
				return s;
			}
		} else {
			String relativePath;
			String prefix;
			int li = pattern.lastIndexOf(File.separator);
			if (li > 0) {
				relativePath = pattern.substring(0, li);
				prefix = pattern.substring(relativePath.length() + 1, pattern.length() - 5);
			} else {
				relativePath = null;
				prefix = pattern.substring(0, pattern.length() - 5);
			}
			LOGGER.warn("finding Jar with {}/{}", relativePath, prefix);
			String result = null;
			String[] paths2;

			if (relativePath != null && isAbsolutePath(relativePath)) {
				//
				// 如果是绝对路径, 则仅适用根目录
				paths2 = new String[]{relativePath};
				relativePath = null;
			} else {
				//
				// 否则遍历当前路径
				paths2 = CollectionUtils.toArray(paths, String.class);
			}

			for (String path : paths2) {
				String p2 = relativePath != null ? path + File.separator + relativePath : path;
				LOGGER.warn("finding Jar in {}", p2);

				File[] files = new File(p2).listFiles();
				if (files == null) {
					continue;
				}

				for (File f : files) {
					if (!f.isDirectory() && f.getName().startsWith(prefix)) {
						if (result == null) {
							result = p2 + File.separator + f.getName();
						} else {
							String msg = "file1=" + result + "\r\nfile2=" + f.getPath();
							throw new IllegalArgumentException("ambiguous jars: " + pattern + " :" + msg);
						}
					}
				}
			}
			if (result == null) {
				throw new IllegalArgumentException("jar not found with:" + pattern);
			}
			return result;
		}
	}

	public String findFiles(String file) {
		for (String path : paths) {
			String fileName = path + File.separator + file;
			File f = new File(fileName);
			if (f.exists()) {
				return fileName;
			}
		}
		return null;
	}

	@Override
	public String getAbsolutePath(String file) {
		if (isAbsolutePath(file)) {
			return file;
		}
		for (String path : paths) {
			String p2 = path + File.separator + file;
			File f = new File(p2);
			if (!f.exists()) {
				continue;
			}
			return p2;
		}
		return null;
	}

	public static void main(String[] args) {
		ConfigProviderImpl c = new ConfigProviderImpl();
		for (String s : c.paths) {
			System.out.println("path:" + s);
		}
		System.out.println("-------------------------");
		c.addClassPath("/Users/Leon/Team/common/helium215/bin", ".:..:../lib:../log");


//		c.recognizeRoot(getParentPath("/Users/Leon/Temp/rcs_as_im/bin"));
//		System.out.println("root=" + c.root);
//		System.out.println("bin=" + c.binPath);
//		System.out.println("logs=" + c.logsPath);
//		System.out.println("libs=" + c.libPath);
//		System.out.println("config=" + c.configPath);
//
//		System.out.println("findJar1 = " + c.findJarFile("helium-dashboard/build/libs/helium-dashboard-2.1.6-SNAPSHOT.jar"));
//		System.out.println("findJar2 = " + c.findJarFile("helium-dashboard/build/libs/helium-dashboard~.jar"));
//		System.out.println("findJar3 = " + c.findJarFile("/Users/Leon/Team/common/helium215/helium-dashboard/build/libs/helium-dashboard-2.1.6-SNAPSHOT.jar"));
//		System.out.println("findJar4 = " + c.findJarFile("/Users/Leon/Team/common/helium215/helium-dashboard/build/libs/helium-dashboard~.jar"));
	}
}

