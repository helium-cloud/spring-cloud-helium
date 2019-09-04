package org.helium.cloud.configcenter.utils;

public class ConfigConvert
{
	public static byte[] long2Bytes(long value)
	{
		if (value != 0)
		{
			int zeros = Long.numberOfLeadingZeros(value);
			int length = 8 - zeros / 8;
			byte[] rawValue = new byte[length];
			for (int i = 0; i < length; i++)
			{
				rawValue[i] = (byte) (value >>> ((i) * 8));
			}
			return rawValue;
		}
		else
		{
			return new byte[]{(byte) 0};
		}
	}

	public static long bytes2Long(byte[] value)
	{
		if (value.length > 8)
			return -1;
		long[] temp = new long[value.length];
		for (int i = 0; i < value.length; i++)
		{
			temp[i] = 0x000000000000FF & value[i];
		}
		long ret = 0;
		for (int i = 0; i < temp.length; i++)
		{
			ret = ((long) (ret | temp[i] << (i * 8)));
		}
		return ret;
	}

	public static int bytes2Int(byte[] value)
	{
		if (value.length > 4)
			return -1;
		int[] temp = new int[value.length];
		for (int i = 0; i < value.length; i++)
		{
			temp[i] = 0x0000FF & value[i];
		}
		int ret = 0;
		for (int i = 0; i < temp.length; i++)
		{
			ret = (ret | temp[i] << (i * 8));
		}
		return ret;
	}

	public static String bytes2String(byte[] value)
	{
		StringBuilder sb = new StringBuilder();
		for (byte b : value)
		{
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}

	/**
	 * Hexadecimal string into an array of bytes
	 * 
	 * @param hexString
	 *            Hexadecimal format string
	 * @return The byte array after conversion
	 **/
	public static byte[] toByteArray(String hexString)
	{
		hexString = hexString.toLowerCase();
		final byte[] byteArray = new byte[hexString.length() / 2];
		int k = 0;
		for (int i = 0; i < byteArray.length; i++)
		{
			// Because it is a hexadecimal, most will only take up to four, converted into bytes need two hexadecimal characters, high first
			byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
			byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
			byteArray[i] = (byte) (high << 4 | low);
			k += 2;
		}
		return byteArray;
	}
	
	public static byte[] hexToBytes(String str) {

		if (null != str && str.length() >= 2) {
			int len = str.length() / 2;
			byte[] buffer = new byte[len];
			for (int i = 0; i < len; i++) {
				buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
			}
			return buffer;
		}
		return null;
	}

	public static byte[] intToByteArray(int i)
	{
		byte[] result = new byte[4];
		result[0] = (byte) (i & 0xFF);
		result[1] = (byte) ((i >> 8) & 0xFF);
		result[2] = (byte) ((i >> 16) & 0xFF);
		result[3] = (byte) ((i >> 24) & 0xFF);
		return result;
	}

	public static byte[] longToByteArray(long l)
	{
		byte[] result = new byte[8];
		result[0] = (byte) (l & 0xFF);
		result[1] = (byte) ((l >> 8) & 0xFF);
		result[2] = (byte) ((l >> 16) & 0xFF);
		result[3] = (byte) ((l >> 24) & 0xFF);
		result[4] = (byte) ((l >> 32) & 0xFF);
		result[5] = (byte) ((l >> 40) & 0xFF);
		result[6] = (byte) ((l >> 48) & 0xFF);
		result[7] = (byte) ((l >> 56) & 0xFF);
		return result;
	}
}
