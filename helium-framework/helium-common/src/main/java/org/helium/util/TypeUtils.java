package org.helium.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coral on 7/25/15.
 */
public class TypeUtils {
	public static boolean isTrue(String s) {
		if (s == null) {
			return false;
		} else {
			return "true".equals(s.toLowerCase());
		}
	}

	public static boolean isFalse(String s) {
		if (s == null) {
			return false;
		} else {
			return "false".equals(s.toLowerCase());
		}
	}

	public static List<String> split(String s, String splitter) {
		List<String> list = new ArrayList<>();
		if (StringUtils.isNullOrEmpty(s)) {
			return list;
		}
		for (String a: s.split(splitter)) {
			if (!"".equals(a)) {
				list.add(a);
			}
		}
		return list;
	}

	public static List<Tuple<String, String>> splitValuePairsList(String str, String delimiter, String assignment)
	{
		List<Tuple<String, String>> ret = new ArrayList<>();
		for (String s: str.split(delimiter)) {
			Outer<String> left = new Outer<String>();
			Outer<String> right = new Outer<String>();
			if (StringUtils.splitWithFirst(s, assignment, left, right)) {
				ret.add(new Tuple<String, String>(left.value().trim(), right.value().trim()));
			} else {
				ret.add(new Tuple<String, String>(s, ""));
			}
		}
		return ret;
	}
}
