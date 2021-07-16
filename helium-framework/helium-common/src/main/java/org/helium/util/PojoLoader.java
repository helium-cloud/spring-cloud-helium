//package org.helium.util;
//
//
//import org.coral.superpojo.SuperPojo;
//
//import java.io.*;
//
//public class PojoLoader {
//	private static Class<?> rootClazz;
//
//	public static void setResourceRoot(Class<?> clazz) {
//		rootClazz = clazz;
//	}
//	/**
//	 *
//	 * 从一个固定文件中读取Xml
//	 *
//	 * @param file
//	 * @param clazz
//	 * @return
//	 * @throws FileNotFoundException
//	 * @throws InstantiationException
//	 * @throws IllegalAccessException
//	 */
//	public static <E extends SuperPojo> E loadFromXml(String file,
//													  Class<E> clazz) throws IOException,
//			InstantiationException, IllegalAccessException {
//		E pojo = clazz.newInstance();
//		String xml = readResourceFile(file);
//		pojo.parseXmlFrom(xml);
//		return pojo;
//	}
//
//	/**
//	 * 读取src/main/resources 下面的文件内容到字符串中
//	 *
//	 * @param fileName
//	 *            文件名 示例：/sip/register.sip 表示
//	 *            src/main/resources/sip/register.sip
//	 * @return
//	 * @throws IOException
//	 */
//	public static String readResourceFile(String fileName) throws IOException {
//		InputStream in = rootClazz.getResourceAsStream(fileName);
//		if (in == null) {
//			throw new FileNotFoundException("file not found:" + fileName);
//		}
//		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
//		String temp = null;
//		StringBuffer sb = new StringBuffer();
//		temp = bufferReader.readLine();
//		while (temp != null) {
//			sb.append(temp + "\r\n");
//			temp = bufferReader.readLine();
//		}
//
//		String str = sb.toString();
//		return str;
//	}
//
//}
