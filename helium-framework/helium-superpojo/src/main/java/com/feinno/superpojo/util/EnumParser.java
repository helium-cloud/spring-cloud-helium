/*
 * FAE, Feinno App Engine
 *  
 * Create by lichunlei 2010-12-16
 * 
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package com.feinno.superpojo.util;

import com.feinno.superpojo.type.EnumInteger;
import com.feinno.superpojo.type.Flags;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * <b>描述: </b>EnumParser是一个可以将int的String表示方式或16进制的String表示方式转为
 * {@link EnumInteger} 的工具类，以及解析成 {@link Flags}
 * <p>
 * <b>功能: </b><br>
 * 1. 将int的String表示方式或16进制的String表示方式转为{@link EnumInteger}<br>
 * 2. 将int的String表示方式或16进制的String表示方式转为{@link Flags}
 * <p>
 * <b>用法: </b>
 * 
 * <pre>
 * 前提必须有一个枚举类型，枚举类型为继承自{@link EnumInteger}
 * public enum TestEnum implements EnumInteger {
 * 	A(0x01),
 * 	B(0x02),
 * 	C(0x04);
 * 	private int value;
 * 	TestEnum(int i) {
 * 		value = i;
 * 	}
 * 	public int intValue() {
 * 		return value;
 * 	}
 * }
 * 
 * 1.EnumInteger的解析示例
 * TestEnum en = (TestEnum) EnumParser.parseInt(TestEnum.class, 1);
 * TestEnum en = (TestEnum) EnumParser.parse(TestEnum.class, "0x0001", true);
 * 
 * 2.Flags的示例
 * Flags<TestEnum> flags = EnumParser.parseFlags(TestEnum.class, "A,B,C", true);
 * 
 * Flags&lt;TestEnum&gt; flags = EnumParser.parseFlags(Flags.class, &quot;3&quot;, true);
 * System.out.println(flags.toString());
 * System.out.println(flags.value());
 * Assert.assertEquals(true, flags.has(TestEnum.A));
 * Assert.assertEquals(true, flags.has(TestEnum.B));
 * </pre>
 * <p>
 * 
 * @author lichunlei
 * @see Flags
 * @see EnumInteger
 */
public class EnumParser
{
	public static Object parse(Class clazz, String enumValue, boolean ignoreCase)
	{
		// enumValue是整数, 则转换
		try {
			Integer i = Integer.parseInt(enumValue);
			return parseInt(clazz, i.intValue());
		} catch (Exception e) {
			return parseString(clazz, enumValue, ignoreCase);
		}

	}

	public static Object parseInt(Class<?> clazz, int value)
	{
		if (clazz.isEnum()) {
			EnumTable t = enums.get(clazz);
			if (t == null) {
				t = new EnumTable(clazz);
				enums.put(clazz, t);
			}
			return t.parse(value);
		} else {
			throw new IllegalArgumentException("Not Enum");
		}
	}

	public static <E extends EnumInteger> E valueOf(Class<E> clazz, int value)
	{
		if (clazz.isEnum()) {
			EnumTable t = enums.get(clazz);
			if (t == null) {
				t = new EnumTable(clazz);
				enums.put(clazz, t);
			}
			return (E) t.parse(value);
		} else {
			throw new IllegalArgumentException("Not Enum");
		}
	}

	public static Object parseString(Class clazz, String enumValue, boolean ignoreCase)
	{
		if (clazz.isEnum()) {
			// 16进制
			if (enumValue.startsWith("0x") || enumValue.startsWith("0X")) {
				String hex = enumValue.substring(2);
				if (hex.length() > 8) {
					throw new IllegalArgumentException("Flag HexToLong: " + enumValue);
				}
				int n = Integer.parseInt(hex, 16);
				return parseInt(clazz, n);
			}
			// String
			if (!ignoreCase)
				return Enum.valueOf(clazz, enumValue);
			else {
				for (Object o : clazz.getEnumConstants()) {
					if (o.toString().equalsIgnoreCase(enumValue))
						return o;
				}
				throw new IllegalArgumentException("Enum does not include Value:" + enumValue);
			}
		} else {
			throw new IllegalArgumentException("Not Enum");
		}
	}

	/**
	 * 
	 * 解析如 "A,B" 之类的枚举
	 * 
	 * @param <E>
	 * @param clazz
	 * @param value
	 * @return
	 */
	public static Flags parseFlags(Class clazz, String enumValue, boolean ignoreCase)
	{
		// 当String是十进制整数, 或16进制整数是, 同样方式获取

		// enumValue是整数, 则转换
		try {
			Integer i = Integer.parseInt(enumValue);
			return new Flags(i.intValue());
		} catch (Exception e) {
			return parseFlagsString(clazz, enumValue, ignoreCase);
		}

	}

	/**
	 * 
	 * 解析如 "A,B" 之类的枚举
	 * 
	 * @param <E>
	 * @param clazz
	 * @param value
	 * @return
	 */
	public static Flags parseFlagsString(Class clazz, String enumValue, boolean ignoreCase)
	{
		// 当String是十进制整数, 或16进制整数是, 同样方式获取

		// 16进制
		if (enumValue.startsWith("0x") || enumValue.startsWith("0X")) {
			String hex = enumValue.substring(2);
			if (hex.length() > 8) {
				throw new IllegalArgumentException("Flag HexToLong: " + enumValue);
			}
			int n = Integer.parseInt(hex, 16);
			return new Flags(n);
		}
		Flags flag = new Flags(0);
		for (String s : enumValue.split(",")) {
			try {
				Object e = parse(clazz, s, ignoreCase);
				flag = flag.or((EnumInteger) e);
			} catch (Exception ex) {
				throw new IllegalArgumentException("Flag Parse Failed:" + enumValue, ex);

			}
		}
		return flag;

	}

	// public static Object parse(String enumValue, Class enumClass, String
	// defaultValue, boolean ignoreUnknown)
	// {
	/*
	 * bool isFlags = AttributeHelper.TryGetAttribute<FlagsAttribute>(enumType)
	 * != null;
	 * 
	 * try { long n = 0; if (enumValue.StartsWith("0x") ||
	 * enumValue.StartsWith("0X")) { string hex = enumValue.Substring(2); if
	 * (hex.Length > 8) { throw new FormatException("Flag HexToLong: " +
	 * enumValue); }
	 * 
	 * n = int.Parse(hex, System.Globalization.NumberStyles.HexNumber); return
	 * Enum.ToObject(enumType, n); } else if (long.TryParse(enumValue, out n)) {
	 * return Enum.ToObject(enumType, n); } else if (isFlags && ignoreUnknown) {
	 * return ParseFlags(enumValue, enumType, true); } else { return
	 * Enum.Parse(enumType, enumValue, true); } } catch (Exception ex) { if
	 * (defaultValue == null) { throw new FormatException("EnumParse Failed:" +
	 * enumValue, ex); } else { return Parse(defaultValue, enumType, "",
	 * ignoreUnknown); } }
	 */
	// return null;
	// }

	// public static Object parseFlags(String enumValue, Class enumClass,
	// boolean ignoreUnknown)
	// {
	/*
	 * long n = 0; foreach (string s in enumValue.Split(',')) { try { object e =
	 * Enum.Parse(enumType, s, true); n |= Convert.ToInt64(e); } catch
	 * (Exception ex) { if (!ignoreUnknown) { throw new
	 * FormatException("Flag Parse Failed:" + enumValue, ex); } } } return
	 * Enum.ToObject(enumType, n);
	 */
	// return null;
	// }

	private static class EnumTable
	{
		private Map<Integer, Object> values = new HashMap<Integer, Object>();

		EnumTable(Class clazz)
		{
			for (Object o : clazz.getEnumConstants()) {
				if (o instanceof EnumInteger) {
					int key = ((EnumInteger) o).intValue();
					values.put(Integer.valueOf(key), o);
				}
			}
		}

		public Object parse(int v)
		{
			return values.get(Integer.valueOf(v));
		}
	}

	private static Map<Class, EnumTable> enums = new ConcurrentHashMap<Class, EnumTable>();
}
