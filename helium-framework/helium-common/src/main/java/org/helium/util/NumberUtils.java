/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2011-1-6
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.util;

/**
 * 
 * <b>描述: </b>对于数字操作的帮助类，例如从数字到字节的转换，或从字节到数字的转换
 * <p>
 * <b>功能: </b>对于数字操作的帮助类
 * <p>
 * <b>用法: </b><pre>
 * 列举两个示例：
 * Assert.assertEquals(128, NumberUtils.NextPower2(100));
 * Assert.assertEquals(100, NumberUtils.byteArrayToInt(new byte[]{0,0,0,100}));
 * </pre>
 * <p>
 *
 * Created by Coral
 *
 */
public class NumberUtils
{
	public static int NextPower2(int a)
	{
		int n = 1;
		for (int i = 0; i < 31; i++) {
			if (a <= n)
				return n;
			n = n << 1;
		}
		throw new IllegalArgumentException("Too Big Number:" + a);
	}

	public static final byte[] intToByteArray(int value)
	{
		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value };
	}

	public static final int byteArrayToInt(byte[] data)
	{
		int totallength = (data.length < 4) ? data.length : 4;

		int value = 0;
		for (int i = 0; i < totallength; i++) {
			value |= (data[i] << ((3 - i) * 8)) & (0xff << ((3 - i) * 8));
		}

		return value;
	}

	public static final byte[] shortToByteArray(short value)
	{
		return new byte[] { (byte) (value >>> 8), (byte) value };
	}

	public static final short byteArrayToShort(byte[] data)
	{
		short value = 0;
		value |= (data[0] << 8 & (0xff << 8)) | data[1];

		return value;
	}

	public static void traceHexString(byte[] buffer)
	{
		for (int i = 0; i < buffer.length; i++) {
			String hex = Integer.toHexString(buffer[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print("0x" + hex.toLowerCase() + ", ");
		}

		System.out.println();
	}

	/**
	 * booleanToLong util implements by Fred
	 * 
	 * @param data
	 * @return
	 */
	public static final long booleanToLong(boolean data)
	{
		return data ? 1L : 0L;
	}

	/**
	 * {在这里补充功能说明}
	 * 
	 * @param b
	 * @return
	 */
	public static int booleanToInt(boolean b)
	{
		return b ? 1 : 0;
	}

	public static int byteArrayToInt32(byte[] buffer, int offset)
	{
		int n = (buffer[offset] & 0x000000FF) << 24;
		n |= (buffer[offset + 1] & 0x000000FF) << 16;
		n |= (buffer[offset + 2] & 0x000000FF) << 8;
		n |= (buffer[offset + 3] & 0x000000FF);
		return n;
	}

	public static short byteArrayToInt16(byte[] buffer, int offset)
	{
		int n = (buffer[offset] & 0x000000FF) << 8;
		n |= (buffer[offset + 1] & 0x000000FF);
		return (short)n;
	}

	public static void fillByteBufferWithInt32(int n, byte[] buffer, int offset)
	{
		buffer[offset] = (byte)((n >> 24) & 0xff);
		buffer[offset + 1] = (byte)((n >> 16) & 0xff);
		buffer[offset + 2] = (byte)((n >> 8) & 0xff);
		buffer[offset + 3] = (byte)(n & 0xff);
	}

	public static void fillByteBufferWithInt16(short n, byte[] buffer, int offset)
	{
		buffer[offset] = (byte)((n >> 8) & 0xff);
		buffer[offset + 1] = (byte)(n & 0xff);		
	}
}
