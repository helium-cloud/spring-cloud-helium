/**
 * 
 */
package org.helium.cloud.configcenter.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author liupengzh
 * 
 */
public class MD5
{
	public static String encode(String inStr)
	{

		MessageDigest md = null;
		String outStr = null;
		try
		{
			md = MessageDigest.getInstance("MD5"); // 可以选中其他的算法如SHA
			byte[] digest = md.digest(inStr.getBytes()); // 返回的是byte[]，要转化为String存储比较方便
			String str = "";
			String tempStr = "";
			for (int i = 0; i < digest.length; i++)
			{
				tempStr = (Integer.toHexString(digest[i] & 0xff));
				if (tempStr.length() == 1)
				{
					str = str + "0" + tempStr;
				}
				else
				{
					str = str + tempStr;
				}
			}
			outStr = str;
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return outStr;
	}
	
	public static String encode(byte[] bytes)
	{

		MessageDigest md = null;
		String outStr = null;
		try
		{
			md = MessageDigest.getInstance("MD5"); // 可以选中其他的算法如SHA
			byte[] digest = md.digest(bytes); // 返回的是byte[]，要转化为String存储比较方便
			String str = "";
			String tempStr = "";
			for (int i = 0; i < digest.length; i++)
			{
				tempStr = (Integer.toHexString(digest[i] & 0xff));
				if (tempStr.length() == 1)
				{
					str = str + "0" + tempStr;
				}
				else
				{
					str = str + tempStr;
				}
			}
			outStr = str;
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return outStr;
	}

	public static String encode3(String inStr)
	{
		MessageDigest md = null;
		String outStr = null;
		try
		{
			md = MessageDigest.getInstance("MD5"); // 可以选中其他的算法如SHA
			byte[] digest1 = md.digest(inStr.getBytes());
			byte[] digest2 = md.digest(digest1);
			byte[] digest = md.digest(digest2); // 返回的是byet[]，要转化为String存储比较方便
			String str = "";
			String tempStr = "";
			for (int i = 0; i < digest.length; i++)
			{
				tempStr = (Integer.toHexString(digest[i] & 0xff));
				if (tempStr.length() == 1)
				{
					str = str + "0" + tempStr;
				}
				else
				{
					str = str + tempStr;
				}
			}
			outStr = str;
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return outStr;
	}

	public static byte[] digest(byte[] source) throws Exception
	{
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(source);
		byte[] digest = md.digest();
		return digest;
	}
	
public static String md5_16(String plainText) {
		
		try {
			return md5_16(plainText.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String md5_16(byte[] plainText) {
		String result = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText);
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString().substring(8, 24);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return result;
	}
}
