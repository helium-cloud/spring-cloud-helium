package com.feinno.superpojo.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feinno.superpojo.NativeSuperPojo;
import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.SuperPojoManager;

public class SuperPojoChecker {

	private static final Logger LOGGER = LoggerFactory.getLogger(SuperPojoChecker.class);

	/**
	 * 验证当前环境变量下所有的可序列化类的准确性,返回验证失败的类的列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List<TwoTuple<String, Exception>> check() throws Exception {
		return check("");
	}

	/**
	 * 验证某一个包目录下所有的可序列化类的准确性,返回验证失败的类的列表
	 * 
	 * @param packageName
	 * @return 返回验证失败的类的列表
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List<TwoTuple<String, Exception>> check(String packageName) throws Exception {
		List<TwoTuple<String, Exception>> failedList = new ArrayList<TwoTuple<String, Exception>>();
		List<Class<?>> taskList = new ArrayList<Class<?>>();
		analyzePackage(packageName, taskList);
		int count = 0;
		int total = taskList.size();
		LOGGER.info("Found {} SuperPojo.", total);
		for (Class<?> clazz : taskList) {
			String classPath = clazz.getName();

			classPath = classPath.replace("/", ".");

			LOGGER.info((++count) + "/" + total + " Check " + classPath);
			progressBar(Float.valueOf(count) / Float.valueOf(total));
			try {
				Class<SuperPojo> superPojoClazz = (Class<SuperPojo>) Class.forName(classPath);
				SuperPojoManager.getSuperPojoBuilder(superPojoClazz.newInstance());
			} catch (Exception e) {
				failedList.add(new TwoTuple<String, Exception>(classPath, e));
			}
		}

		LOGGER.info("-------------------------------------------------");
		LOGGER.info("total  : {}", total);
		LOGGER.info("sucess : {}", (total - failedList.size()));
		LOGGER.info("failed : {}", failedList.size());
		if (failedList.size() > 0) {
			for (TwoTuple<String, Exception> twoTuple : failedList) {
				LOGGER.error("Failed : " + twoTuple.getFirst());
			}
		}
		return failedList;
	}

	/**
	 * 分析某一个包路径下的类
	 * 
	 * @param packageName
	 * @param taskList
	 * @throws Exception
	 */
	private static void analyzePackage(String packageName, List<Class<?>> taskList) throws Exception {
		if (packageName == null || packageName.trim().length() == 0) {
			packageName = "";
		} else {
			packageName = packageName.replaceAll("\\.", "/");
			if (!packageName.endsWith("/")) {
				packageName += "/";
			}
		}
		try {
			LOGGER.info("Load package : {}", packageName);
			Enumeration<URL> enumeration = Thread.currentThread().getContextClassLoader().getResources(packageName);
			while (enumeration.hasMoreElements()) {
				URL url = (URL) enumeration.nextElement();
				String protocol = url.getProtocol();
				if (protocol.equals("jar")) {
					analyzeJAR(url, taskList);
				} else {
					analyzeFile(url, packageName, taskList);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("loadPackage", e);
		}
	}

	/**
	 * 处理jar中的class
	 * 
	 * @param url
	 * @throws IOException
	 */
	private static void analyzeJAR(URL url, List<Class<?>> taskList) throws Exception {
		JarURLConnection con = (JarURLConnection) url.openConnection();
		JarFile file = con.getJarFile();
		Enumeration<?> enumeration = file.entries();
		while (enumeration.hasMoreElements()) {
			JarEntry element = (JarEntry) enumeration.nextElement();
			String entryName = element.getName();
			if (entryName != null && entryName.endsWith(".class")) {
				analyzeClass(entryName, taskList);
			}
		}
	}

	/**
	 * 处理文件目录中的class
	 * 
	 * @param url
	 * @throws URISyntaxException
	 */
	private static void analyzeFile(URL url, String packageName, List<Class<?>> taskList) throws Exception {
		File file = new File(new URI(url.toExternalForm()));
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				analyzeFile(files[i].toURI().toURL(), packageName + files[i].getName() + "/", taskList);
			} else {
				String entryName = packageName + files[i].getName();
				if (entryName != null && entryName.endsWith(".class")) {
					analyzeClass(entryName, taskList);
				}
			}
		}
	}

	private static void analyzeClass(String path, List<Class<?>> taskList) throws Exception {
		LOGGER.info("Read " + path); //
		path = path.substring(0, path.lastIndexOf(".class")).replaceAll("/", ".");
		Class<?> clazz = Class.forName(path);
		if (SuperPojo.class.isAssignableFrom(clazz) && !clazz.equals(SuperPojo.class)
				&& !clazz.equals(NativeSuperPojo.class)) {
			taskList.add(clazz);
		}
	}

	private static void progressBar(float rate) {
		if (rate > 1) {
			rate = 1;
		} else if (rate < 0) {
			rate = 0;
		}
		System.out.println("__________________________________________________");
		rate = rate * 50;
		int rateI = (int) rate;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < rateI; i++) {
			sb.append("█");
		}
		System.out.print(sb.toString());
		sb = new StringBuffer();
		for (int i = 0; i < 50 - rateI; i++) {
			sb.append("_");
		}
		sb.append("▏");
		sb.append((rateI * 2) + "%");
		System.out.println(sb.toString());
	}

	// /**
	// * @param args
	// */
	// public static void main(String[] args) throws Exception {
	// SuperPojoChecker.check();
	// SuperPojoChecker.check("com.feinno.imps.legacy");
	// String path = "org/slf4j/impl/SimpleLoggerFactory.class";
	// path = path.substring(0, path.lastIndexOf(".class")).replaceAll("/",
	// ".");
	// System.out.println(path);
	// }
}
