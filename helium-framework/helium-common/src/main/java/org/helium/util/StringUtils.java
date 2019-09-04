/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2011-3-23
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.util;

import java.util.HashMap;
import java.util.Map;

/**
 * <b>描述: </b>字符串实用类
 * <p>
 * <b>功能: </b>为字符串提供一些截取、分割之类的工具方法
 * <p>
 * <b>用法: </b>正常的静态方法调用
 * <p>
 *
 * Created by Coral
 */
public class StringUtils {
	public static final String EMPTY = "";
	public static final String NULL_STRING = "null";
	public static final String[] EMPTY_STRINGS = new String[0];

	public static final int MAX_BUFFER_OUTPUT = 1024;


	/**
	 * 判断是否null或""
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(String str) {
		return str == null ? true : str.equals(EMPTY);
	}

	/**
	 * 安全的toString，增加空的判断
	 * @param v
	 * @return
	 */
	public static String toString(Object v) {
		return toString(v, NULL_STRING);
	}

	/**
	 * 安全的toString，增加空的判断
	 * @param v
	 * @param nullStr
	 * @return
	 */
	public static String toString(Object v, String nullStr) {
		if (v == null) {
			return nullStr;
		} else {
			return v.toString();
		}
	}

	/**
	 * 比较两个字符串是否相等，忽略大小写
	 * @param lv
	 * @param rv
	 * @return
	 */
	public static boolean equalsIgnoreCase(String lv, String rv) {
		if (lv == null) {
			return rv == null;
		} else {
			if (rv == null) {
				return false;
			} else {
				return lv.equalsIgnoreCase(rv);
			}
		}
	}

	/**
	 * 比较两个字符串是否相等，忽略大小写
	 * @param lv
	 * @param rv
	 * @return
	 */
	public static boolean equals(String lv, String rv) {
		if (lv == null) {
			return rv == null;
		} else {
			if (rv == null) {
				return false;
			} else {
				return lv.equals(rv);
			}
		}
	}

	/**
	 * 按照第一个分隔符的位置将字符传分成前后两份
	 *
	 * @param s
	 * @param delimiter
	 * @param first
	 * @param left
	 * @return
	 */
	public static boolean splitWithFirst(String s, char delimiter, Outer<String> first, Outer<String> left) {
		int f = s.indexOf(delimiter);

		if (f > 0) {
			first.setValue(s.substring(0, f));
			left.setValue(s.substring(f + 1));
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 按照第一个分隔符的位置将字符传分成前后两份
	 *
	 * @param s
	 * @param delimiter
	 * @param first
	 * @param left
	 * @return
	 */
	public static boolean splitWithFirst(String s, String delimiter, Outer<String> first, Outer<String> left) {
		int f = s.indexOf(delimiter);

		if (f > 0) {
			first.setValue(s.substring(0, f));
			left.setValue(s.substring(f + delimiter.length()));
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 寻找最后一个分隔符，将字符串分为前后两部分
	 *
	 * @param s
	 * @param delimiter
	 * @param left
	 * @param last
	 * @return
	 */
	public static boolean splitWithLast(String s, char delimiter, Outer<String> left, Outer<String> last) {
		int f = s.lastIndexOf(delimiter);

		if (f > 0) {
			left.setValue(s.substring(0, f));
			last.setValue(s.substring(f + 1));
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 寻找最后一个分隔符，将字符串分为前后两部分
	 *
	 * @param s
	 * @param delimiter
	 * @param left
	 * @param last
	 * @return
	 */
	public static boolean splitWithLast(String s, String delimiter, Outer<String> left, Outer<String> last) {
		int f = s.lastIndexOf(delimiter);

		if (f > 0) {
			left.setValue(s.substring(0, f));
			last.setValue(s.substring(f + delimiter.length()));
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param s
	 * @param delimiter
	 * @return
	 */
	public static String getLastString(String s, String delimiter) {
		int f = s.lastIndexOf(delimiter);
		if (f > 0) {
			return s.substring(f + delimiter.length());
		} else {
			return null;
		}
	}

	/**
	 * 把16进制数字字符串转化成为byte数组
	 *
	 * @param hex
	 * @return byte[]
	 */
	public static byte[] fromHexString(String hex) {
		if (hex == null || hex.length() < 1)
			return new byte[0];

		int len = hex.length() / 2;
		byte[] result = new byte[len];
		len *= 2;

		for (int index = 0; index < len; index++) {
			String s = hex.substring(index, index + 2);
			int b = Integer.parseInt(s, 16);
			result[index / 2] = (byte) b;
			index++;
		}
		return result;
	}

	/**
	 * 去除结尾的特定字符
	 *
	 * @param s
	 * @param c
	 * @return
	 */
	public static String trimEnd(String s, char c) {
		String result = s;
		while (result.endsWith(String.valueOf(c))) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	/**
	 * 分割形如a=1;b=2;c=3形式的字符串
	 *
	 * @param str
	 * @param delimiter
	 * @param assignment
	 * @return
	 */
	public static Map<String, String> splitValuePairs(String str, String delimiter, String assignment) {
		Map<String, String> ret = new HashMap<String, String>();
		for (String s : str.split(delimiter)) {
			Outer<String> left = new Outer<String>();
			Outer<String> right = new Outer<String>();
			if (splitWithFirst(s, assignment, left, right)) {
				ret.put(left.value().trim(), right.value().trim());
			} else {
				ret.put(s, "");
			}
		}
		return ret;
	}

	/**
	 * 分割字符串，适配空串
	 *
	 * @param str
	 * @param splitter
	 * @return
	 */
	public static String[] split(String str, String splitter) {
		if (isNullOrEmpty(str)) {
			return EMPTY_STRINGS;
		} else {
			return str.split(splitter);
		}
	}

	/**
	 * 判断两个字符串是否相等, 包含空值判定
	 *
	 * @param lv
	 * @param rv
	 * @return
	 */
	public static boolean strEquals(String lv, String rv) {
		if (lv == null) {
			return rv == null;
		} else {
			return lv.equals(rv);
		}
	}

	/**
	 * 格式化一段buffer缓冲区, 输出为16进制格式
	 *
	 * @param
	 * @return
	 */
	public static String formatBuffer(byte[] buffer) {
		if (buffer == null) {
			return "<NULL>";
		} else {
			int len = buffer.length;
			StringBuilder s = new StringBuilder();
			s.append("<");
			s.append(len);
			s.append(":");
			if (len > MAX_BUFFER_OUTPUT)
				len = MAX_BUFFER_OUTPUT;
			for (int i = 0; i < len && i < len; i++) {
				byte b = buffer[i];
				if ((b & 0xf0) != 0) {
					s.append(String.format(" %X", b));
				} else {
					s.append(String.format(" 0%X", b));
				}
			}
			s.append(">");
			return s.toString();
		}
	}


	public static String safeTruncate(String str, int len) {
		if (str.length() <= len)
			return str;
		else
			return str.substring(0, len);
	}

	/**
	 * 将一个字符串进行标准化处理。
	 * 1、如果str是null，则设置成空串
	 * 2、如果str包含左右空格，则去掉。
	 *
	 * @return 返回一个标准化处理后的字符串。
	 */
	public static String normalizeWithTrim(String str) {
		if (str == null) {
			return EMPTY;
		} else if (str.length() == 0) {
			return EMPTY;
		} else {
			str = str.trim();
			if (str.length() == 0) {
				return EMPTY;
			} else {
				return str;
			}
		}
	}

	public static String substring(String str, int len) {
		if (str.length() <= len)
			return str;
		else
			return str.substring(0, len);
	}

	/**
	 * 截取字符串到合适的长度
	 * @param s 原串
	 * @param blackLength 截取长度
	 * @param trimAppend
	 * @return
	 */
	public static String trimToLength(String s, int blackLength, String trimAppend) {
		if (isNullOrEmpty(s)) return s;
		char[] charArray = s.toCharArray();
		int cutLength = 0;
		int i = 0;
		for (; i < charArray.length; i++) {
			if (cutLength >= blackLength)//先预测一下
				break;
			if ((int) charArray[i] > 128)
				cutLength += 2;//中文
			else
				cutLength += 1;
			if (cutLength > blackLength)//在判断一下，有可能差1，加2
				break;
		}
		StringBuilder sb = new StringBuilder(s.substring(0, i));
		if (sb.toString().length() < s.length()) { //表示有截取，如果直接加上"…"，会超长
			int pos = cutLength - blackLength;//当cutLength+1==blackLength的位置为中文字符时，正好多一位，所以这里的pos只有两种值0,1
			if ((int) charArray[i - 1] > 128) {
				sb.delete(sb.length() - 1, sb.length());//如果最后的字符是中文，移除一位即可满足“…”的长度
			} else { //如果是英文需要移除两个字符
				if (pos > 0)
					sb.delete(sb.length() - 1, sb.length());//原来少一位，现在只需移除一个即可
				else
					sb.delete(sb.length() - 2, sb.length());
			}
			sb.append(trimAppend);
		}
		return sb.toString();
	}
}
