/*
 * FAE, Feinno App Engine
 *  
 * Create by Huangxianglong 2011-7-13
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.util;

/**
 *
 * <b>描述: </b>顾名思义，XML的工具类，为XML格式的字符串提供了特殊字符转换的操作
 * <p>
 * <b>功能: </b>为XML格式的字符串提供了特殊字符转换的操作
 * <p>
 * <b>用法: </b>正常的工具类调用方式
 * <p>
 *
 * @author huangxianglong
 *
 */
public class XmlUtils {
	/**
	 * encode字段中的非法字符
	 * @param s
	 * @return
	 */
	public static String encode(String s) {
		return encode(s, true);
	}

	/**
	 * encode字段中的非法字符
	 * @param s
	 * @param removeInvalidChar 是否移除非法字符
	 * @return
	 */
	public static String encode(String s, boolean removeInvalidChar) {
		if (StringUtils.isNullOrEmpty(s))
			return s;

		StringBuilder ret = null;
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);

			String ts = null;
			boolean invalidChar = false;

			if (isValidCharCode(ch) || ch == '\t' || ch == '\r' || ch == '\n') {
				switch (ch) {
					case '<':
						ts = "&lt;";
						break;
					case '>':
						ts = "&gt;";
						break;
					case '\"':
						ts = "&quot;";
						break;
					case '\'':
						ts = "&apos;";
						break;
					case '&':
						ts = "&amp;";
						break;
					case '\n':
						ts = "&#xa;";
						break;
					case '\r':
						ts = "&#xd;";
						break;
					default:
						break;
				}
			} else {
				invalidChar = true;
			}

			if (ret == null && (ts != null || invalidChar)) {
				ret = new StringBuilder();
				ret.append(s.substring(0, i));
			}

			if (ts != null) {
				ret.append(ts);
			} else if (invalidChar) {
				if (!removeInvalidChar)
					ret.append("&#x").append((Integer.toHexString((int) ch))).append(";");

			} else if (ret != null) {
				ret.append(ch);
			}
		}

		if (ret == null)
			return s;
		else
			return ret.toString();
	}

	/**
	 * 将xml字段解码为原始字段
	 * @param str
	 * @return
	 */
	public static String decode(String str) {
		str = str.replace("&lt;", "<");
		str = str.replace("&gt;", ">");
		str = str.replace("&quot;", "\"");
		str = str.replace("&apos;", "\'");
		str = str.replace("&amp;", "&");
		str = str.replace("&#xd;","\r");
		str = str.replace("&#xa;","\n");

		return str;
	}

	/**
	 * 是否合法编码
	 * @param code
	 * @return
	 */
	private static boolean isValidCharCode(int code) {
		return (0x0020 <= code && code <= 0xD7FF) || (0x000A == code) || (0x0009 == code) || (0x000D == code) || (0xE000 <= code && code <= 0xFFFD) || (0x10000 <= code && code <= 0x10ffff);
	}
}
