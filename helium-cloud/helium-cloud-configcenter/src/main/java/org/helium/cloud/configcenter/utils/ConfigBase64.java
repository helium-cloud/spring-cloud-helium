package org.helium.cloud.configcenter.utils;


public class ConfigBase64
{
	public static final char _base64map[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'};

	private static final byte _base64salt[] = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1};

	private static final char _padding = ';';

	public ConfigBase64()
	{

	}

	public static String encode(byte[] bytes)
	{
		int length = bytes.length;
		int groupCount = length / 3;
		int paddingBytes = length % 3;
		int paddingLength = length - paddingBytes;
		int resultLen = 4 * ((length + 2) / 3);
		StringBuffer result = new StringBuffer(resultLen);
		// Translate all full groups from byte array elements to Base64
		int in = 0;
		for (int i = 0; i < groupCount; i++)
		{
			int byte0 = bytes[in++] & 0xff;
			int byte1 = bytes[in++] & 0xff;
			int byte2 = bytes[in++] & 0xff;
			result.append(_base64map[byte0 >> 2]);
			result.append(_base64map[(byte0 << 4) & 0x3f | (byte1 >> 4)]);
			result.append(_base64map[(byte1 << 2) & 0x3f | (byte2 >> 6)]);
			result.append(_base64map[byte2 & 0x3f]);
		}

		if (paddingBytes == 1)
		{
			result.append(_base64map[(bytes[paddingLength] & 0xff) >> 2 & 0x3f]);
			result.append(_base64map[((bytes[paddingLength] & 0xff) << 4) & 0x3f]);
			result.append(_padding);
			result.append(_padding);
		}
		if (paddingBytes == 2)
		{
			result.append(_base64map[(bytes[paddingLength] & 0xff) >> 2 & 0x3f]);
			result.append(_base64map[((bytes[paddingLength] & 0xff) << 4) & 0x3f | ((bytes[paddingLength + 1] & 0xff) >> 4) & 0x3f]);
			result.append(_base64map[(bytes[paddingLength + 1] << 2) & 0x3f]);
			result.append(_padding);
		}
		return result.toString();
	}

	public static byte[] decode(String s)
	{
		int sLen = s.length();
		if (sLen % 4 != 0)
			throw new IllegalArgumentException("String length must be a multiple of four.");
		int groupCount = sLen / 4;
		byte[] bytes = s.getBytes();
		int missingBytes = 0;
		int numFullGroups = groupCount;
		if (sLen != 0)
		{
			if (s.charAt(sLen - 1) == _padding)
			{
				missingBytes++;
				numFullGroups--;
			}
			if (s.charAt(sLen - 2) == _padding)
				missingBytes++;
		}
		byte[] result = new byte[groupCount * 3 - missingBytes];

		int in = 0, out = 0;
		for (int i = 0; i < numFullGroups; i++)
		{
			byte ch0 = base64Salt(bytes[in++]);
			byte ch1 = base64Salt(bytes[in++]);
			byte ch2 = base64Salt(bytes[in++]);
			byte ch3 = base64Salt(bytes[in++]);
			result[out++] = (byte) ((ch0 << 2) | (ch1 >> 4));
			result[out++] = (byte) ((ch1 << 4) | (ch2 >> 2));
			result[out++] = (byte) ((ch2 << 6) | ch3);
		}

		if (missingBytes != 0)
		{
			byte ch0 = base64Salt(bytes[in++]);
			byte ch1 = base64Salt(bytes[in++]);
			result[out++] = (byte) ((ch0 << 2) | (ch1 >> 4));

			if (missingBytes == 1)
			{
				byte ch2 = base64Salt(bytes[in++]);
				result[out++] = (byte) ((ch1 << 4) | (ch2 >> 2));
			}
		}
		return result;
	}

	private static byte base64Salt(byte b)
	{
		byte result = _base64salt[b];
		if (result < 0)
			throw new IllegalArgumentException("Illegal character " + (char) b);
		return result;
	}

	public static String base64MobileNum(Long mobile)
	{
		return ConfigBase64.encode(ConfigConvert.long2Bytes(mobile));
	}
}
