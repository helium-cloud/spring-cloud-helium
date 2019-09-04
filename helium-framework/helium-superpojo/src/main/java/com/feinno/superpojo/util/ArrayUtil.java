package com.feinno.superpojo.util;

import java.util.List;

/**
 * <b>描述: </b>序列化组件中使用，将List转化为原始数据类型数组的工具类，因为List无法导出原始数据类型的数组，
 * 通过此工具类可以弥补这方面的劣势.
 * <p>
 * <b>功能: </b>可以使List导出原始数据类型的数组
 * <p>
 * <b>用法: </b>该类为静态方法类，所有方法采用静态调用既可
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class ArrayUtil {

	/**
	 * 将List填充置入参的数组中
	 * 
	 * @param list
	 * @param t
	 */
	public static <T> void listToArray(List<?> list, T[] t) {
		list.toArray(t);
	}

	/**
	 * 将List填充置入参的原始数据int类型的数组中
	 * 
	 * @param list
	 * @param t
	 */
	public static void listToArray(List<?> list, int[] t) {
		for (int i = 0; i < list.size(); i++) {
			t[i] = (Integer) list.get(i);
		}
	}

	/**
	 * 将List填充置入参的原始数据long类型的数组中
	 * 
	 * @param list
	 * @param t
	 */
	public static void listToArray(List<?> list, long[] t) {
		for (int i = 0; i < list.size(); i++) {
			t[i] = (Long) list.get(i);
		}
	}

	/**
	 * 将List填充置入参的原始数据float类型的数组中
	 * 
	 * @param list
	 * @param t
	 */
	public static void listToArray(List<?> list, float[] t) {
		for (int i = 0; i < list.size(); i++) {
			t[i] = (Float) list.get(i);
		}
	}

	/**
	 * 将List填充置入参的原始数据double类型的数组中
	 * 
	 * @param list
	 * @param t
	 */
	public static void listToArray(List<?> list, double[] t) {
		for (int i = 0; i < list.size(); i++) {
			t[i] = (Double) list.get(i);
		}
	}

	/**
	 * 将List填充置入参的原始数据boolean类型的数组中
	 * 
	 * @param list
	 * @param t
	 */
	public static void listToArray(List<?> list, boolean[] t) {
		for (int i = 0; i < list.size(); i++) {
			t[i] = (Boolean) list.get(i);
		}
	}

	/**
	 * 将List填充置入参的原始数据short类型的数组中
	 * 
	 * @param list
	 * @param t
	 */
	public static void listToArray(List<?> list, short[] t) {
		for (int i = 0; i < list.size(); i++) {
			t[i] = (Short) list.get(i);
		}
	}

	/**
	 * 将List填充置入参的原始数据byte类型的数组中
	 * 
	 * @param list
	 * @param t
	 */
	public static void listToArray(List<?> list, byte[] t) {
		for (int i = 0; i < list.size(); i++) {
			t[i] = (Byte) list.get(i);
		}
	}

	/**
	 * 将List填充置入参的原始数据char类型的数组中
	 * 
	 * @param list
	 * @param t
	 */
	public static void listToArray(List<?> list, char[] t) {
		for (int i = 0; i < list.size(); i++) {
			t[i] = (Character) list.get(i);
		}
	}

	/**
	 * 将包装类型的字节数组转换成原始数据类型的字节数组
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] wrapsToPrimitive(Byte[] bytes) {
		byte[] result = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			result[i] = bytes[i];
		}
		return result;
	}

	/**
	 * 将List中的字节转换为原始字节类型的数组
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] wrapsToPrimitive(List<?> bytes) {
		byte[] result = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++) {
			result[i] = (Byte) bytes.get(i);
		}
		return result;
	}

	/**
	 * 将原始字节类型的数组转换为包装类型的数组
	 * 
	 * @param bytes
	 * @return
	 */
	public static Byte[] primitiveToWraps(byte[] bytes) {
		Byte[] result = new Byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			result[i] = bytes[i];
		}
		return result;
	}
}
