package org.helium.util;

/**
 * Created by Coral on 3/4/16.
 */
public class StringParser {
	private int p;
	private int length;
	private String str;

	public StringParser(String s) {
		this.str = s;
		this.length = s.length();
	}

	/**
	 * @return
	 */
	public boolean hasMore() {
		return p < length;
	}

	/**
	 * 获取Delimiter前的字符串, 不论delimiter是否存在
	 *
	 * @param delimiter
	 * @return
	 */
	public String getToken(String delimiter) {
		return getToken(delimiter, true);
	}

	/**
	 * 获取Delimiter前的字符串, 不论delimiter是否存在
	 *
	 * @param delimiter
	 * @return
	 */
	public String getTokenIgnoreCase(String delimiter) {
		return getToken(delimiter, true, true);
	}

	/**
	 * 是否存在参数
	 *
	 * @param token
	 * @return
	 */
	public boolean hasToken(String token) {
		return str.indexOf(token.toUpperCase(), p) > 0;
	}

	/**
	 * 是否存在参数  忽略大小写
	 *
	 * @param token
	 * @return
	 */
	public boolean hasTokenIgnoreCase(String token) {
		String upStr = str.toUpperCase();
		return upStr.indexOf(token, p) > 0;
	}

	/**
	 * 获取delimeter前的字符,不论delimiter是否存在
	 *
	 * @param delimiter
	 * @return
	 */
	public String getToken(String delimiter, boolean skipDelimiter) {
		if (!hasMore()) {
			return null;
		}
		int l = str.indexOf(delimiter, p);
		if (l >= 0) {
			String r = str.substring(p, l);
			p = l + (skipDelimiter ? delimiter.length() : 0);
			return r;
		} else {
			return getLast();
		}
	}

	/**
	 * 获取delimeter前的字符,不论delimiter是否存在
	 * isIgnoreCase 是否忽略大小写
	 *
	 * @param delimiter
	 * @return
	 */
	public String getToken(String delimiter, boolean skipDelimiter, boolean isIgnoreCase) {
		if (!hasMore()) {
			return null;
		}
		int l = str.indexOf(delimiter, p);
		if (isIgnoreCase) {
			String upStr = str.toUpperCase();
			l = upStr.indexOf(delimiter.toUpperCase(), p);
		}
		if (l >= 0) {
			String r = str.substring(p, l);
			p = l + (skipDelimiter ? delimiter.length() : 0);
			return r;
		} else {
			return getLast();
		}
	}

	/**
	 * 读到下一个分隔符, 如果分隔符内有
	 *
	 * @param assignment
	 * @param delimiter
	 * @return
	 */
	public StringPair getPair(String assignment, String delimiter) {
		if (!hasMore()) {
			return null;
		}
		String pairStr = getToken(delimiter);
		StringParser pairParser = new StringParser(pairStr);

		StringPair ret = new StringPair();
		ret.name = pairParser.getToken(assignment);
		ret.value = pairParser.getLast();
		return ret;
	}

	/**
	 * 获取剩余字符串
	 *
	 * @return
	 */
	public String getLast() {
		if (!hasMore()) {
			return null;
		}
		String r = str.substring(p);
		p = str.length();
		return r;
	}

	/**
	 * 消费n个字符
	 *
	 * @param n
	 * @return false表示字符串已经被消费完
	 */
	public boolean consume(int n) {
		p += n;
		return p >= length;
	}

	public static class StringPair {
		private String name;
		private String value;

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public String toString() {
			return name + "=" + value;
		}
	}
}
