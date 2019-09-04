/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2011-3-23
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package com.feinno.superpojo.util;

import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 
 * <b>描述: </b>字符串实用类
 * <p>
 * <b>功能: </b>为字符串提供一些截取、分割之类的工具方法
 * <p>
 * <b>用法: </b>正常的静态方法调用
 * <p>
 * 
 * @author 高磊 gaolei@feinno.com
 * 
 */
public class StringUtils
{
	public static final String EMPTY = "";

	public static boolean splitWithFirst(String s, char delimiter, Outter<String> first, Outter<String> left)
	{
		int f = s.indexOf(delimiter);

		if (f > 0) {
			first.setValue(s.substring(0, f));
			left.setValue(s.substring(f + 1));
			return true;
		} else {
			return false;
		}
	}

	public static boolean splitWithFirst(String s, String delimiter, Outter<String> first, Outter<String> left)
	{
		int f = s.indexOf(delimiter);

		if (f > 0) {
			first.setValue(s.substring(0, f));
			left.setValue(s.substring(f + delimiter.length()));
			return true;
		} else {
			return false;
		}
	}

	public static boolean splitWithLast(String s, char delimiter, Outter<String> left, Outter<String> last)
	{
		int f = s.lastIndexOf(delimiter);

		if (f > 0) {
			left.setValue(s.substring(0, f));
			last.setValue(s.substring(f + 1));
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean splitWithLast(String s, String delimiter, Outter<String> left, Outter<String> last)
	{
		int f = s.lastIndexOf(delimiter);

		if (f > 0) {
			left.setValue(s.substring(0, f));
			last.setValue(s.substring(f + delimiter.length()));
			return true;
		} else {
			return false;
		}
	}	

	public static boolean extractQuoted(String s, char quotaOpen, char quotaClose, Outter<String> quotedValue,
			Outter<String> first, Outter<String> left)
	{
		throw new UnsupportedOperationException("还没实现");
	}

	/**
	 * 把16进制数字字符串转化成为byte数组
	 * 
	 * @param hex
	 * @return byte[]
	 */
	public static byte[] fromHexString(String hex)
	{
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

	public static boolean isNullOrEmpty(String str)
	{
		return str == null ? true : str.equals(EMPTY);
	}
	
	public static String trimEnd(String s,char c){
		String result=s;
		while(result.endsWith(String.valueOf(c))){
			result=result.substring(0,result.length()-1);
		}
		return result;

	}
	
	public static Map<String, String> splitValuePairs(String str, String delimiter, String assignment)
	{
		Hashtable<String, String> ret = new Hashtable<String, String>();
		for (String s: str.split(delimiter)) {
			Outter<String> left = new Outter<String>();
			Outter<String> right = new Outter<String>();
			if (splitWithFirst(s, assignment, left, right)) {
				ret.put(left.value().trim(), right.value().trim());	
			} else {
				ret.put(s, "");
			}
		}
		return ret;
	}

	/**
	 * 格式化一段buffer缓冲区
	 * @param contextData
	 * @return
	 */
	public static String formatBuffer(byte[] buffer)
	{
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
	
	/// <summary>
	/// 检查前len位是不是全半角
	/// </summary>
	/// <param name="str"></param>
	/// <param name="len"></param>
	/// <returns></returns>
	public static boolean isAllDBC(String str, int len)
	{
		boolean rt = true;
		for (int i = 0; i < len && i < str.length(); i++)
			if (str.charAt(i) > 0x00FE)
				return false;
		return rt;
	}
	/// <summary>
	/// 是否全半角字串。
	/// </summary>
	/// <param name="str"></param>
	/// <returns></returns>
	public static boolean isAllDBC(String str)
	{
		boolean rt = true;
		for (int i = 0; i < str.length(); i++)
			if (str.charAt(i) > 0x00FE)
				return false;
		return rt;
	}
    /// <summary>
    /// 一个字符转半角
    /// </summary>
    /// <param name="a"></param>
    /// <returns></returns>
    public static char toDBC(char a)
    {
        if ((a >= '!' && a <= '~'))
            return a;
        else if ((a >= '！' && a <= '～'))
            return (char)((a & 0x00ff) + 0x0020);
        else
            return a;
    }

    ///// <summary>
    ///// 一个字串转半角
    ///// </summary>
    ///// <param name="a"></param>
    ///// <returns></returns>
    //public static string ToDBC(string a)
    //{
    //    string rtstr = "";
    //    for (int i = 0; i < a.Length; i++)
    //        rtstr += ToDBC(a[i]);
    //    return rtstr;
    //}

    //修改说明：
    //考虑到原全半角转换方法内存开销巨大的问题，故将其替换
    //ToDBC(string a)与ToSBC(string a)已被新方法代替，但为方便回滚，故仅注释掉
    //但不知其关联的ToDBC(char a)和ToSBC(char a)是否被他其他方法依赖，故暂时保留
    //新方法经开发测试，未发现问题，但实际效果仍需测试人员测试确认

    /// <summary>一个字串转半角</summary>
    /// <param name="input">任意字符串</param>
    /// <returns>半角字符串</returns>
    ///<remarks>
    ///全角空格为12288，半角空格为32
    ///其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
    ///</remarks>
    public static String toDBC(String input)
    {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++)
        {
            if (c[i] == 12288)
            {
                c[i] = (char)32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char)(c[i] - 65248);
        }
        return new String(c);
    }

    /// <summary>
    /// 一个字符转全角
    /// </summary>
    /// <param name="a"></param>
    /// <returns></returns>
    public static char toSBC(char a)
    {
        if ((a >= '！' && a <= '～'))
            return a;
        else if ((a >= '!' && a <= '~'))
            return (char)((a - 0x0020) | 0xff00);
        else
            return a;
    }

    ///// <summary>
    ///// 一个字串转全角
    ///// </summary>
    ///// <param name="a"></param>
    ///// <returns></returns>
    //public static string ToSBC(string a)
    //{
    //    string rtstr = "";
    //    for (int i = 0; i < a.Length; i++)
    //        rtstr += ToSBC(a[i]);
    //    return rtstr;
    //}

    /// <summary>
    /// 一个字串转全角
    /// </summary>
    /// <param name="input">任意字符串</param>
    /// <returns>全角字符串</returns>
    ///<remarks>
    ///全角空格为12288，半角空格为32
    ///其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
    ///</remarks>
    public static String toSBC(String input)
    {
        //半角转全角：
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++)
        {
            if (c[i] == 32)
            {
                c[i] = (char)12288;
                continue;
            }
            if (c[i] < 127)
                c[i] = (char)(c[i] + 65248);
        }
        return new String(c);
    }
	
	public static final int MAX_BUFFER_OUTPUT = 1024;

	/// <summary>
	/// 截取字符串，全角长度算2个
	/// </summary>
	/// <param name="source"></param>
	/// <param name="length"></param>
	/// <returns></returns>
	public static String truncateStringWithDBC(String source, int length)
	{
		int dbcCount = 0;
		for (int i = 0; i < length && i < source.length(); i++) {
			//全角字符
			if (source.charAt(i) > 0x00FE)
				dbcCount += 2;
			else
				dbcCount += 1;
			if (dbcCount >= length)
				return source.substring(0, i + 1);
		}
		return source;
	}
	
    public static String[] getNewStrArray(int count)
    {
        String[] strs = new String[count];
        for (int i = 0; i < count; i++)
            strs[i] = "";
        return strs;
    }
    
    public static boolean isNum(String str)
    {
        boolean issid = false;
        Pattern regex =  Pattern.compile("^[0-9]*$");//Pattern.compile("^(?<num>[0-9])");//考滤放配置文件
        issid = regex.matcher(str).matches();
        return issid;
    }
    
    public static int str2int(String str, int def)
    {
        try {
            if (str.length() == 0)
                return def;
            str = toDBC(str);
            return Integer.parseInt(str);
        } catch (Exception e){

            return def;
        }

    }

    public static long str2int(String str, long def)
    {

        try {
            if (str.length() == 0)
                return def;
            str = toDBC(str);
            return Long.parseLong(str);
        } catch (Exception e ){

            return def;
        }

    }
    /// <summary>
    /// i.tostring().length>length return i.tostring(
    /// </summary>
    /// <param name="i"></param>
    /// <param name="length"></param>
    /// <param name="fillwith"></param>
    /// <returns></returns>
    public static String int2str(int i, int length, String fillwith)
    {
        String rt = String.valueOf(i);
        if (fillwith.length() == 0)
            return rt;
        if (rt.length() >= length)
            return rt;

        rt = String.format("{" + fillwith + ":D" + length+ "}", i);
        return rt;
    }
    
    
	public static String substring(String str, int len)
	{
		if (str.length() <= len)
			return str;
		else
			return str.substring(0, len);
	}

	public static boolean strEquals(String lval, String rval)
	{
		if (lval == null) {
			return rval == null;
		} else {
			return lval.equals(rval);
		}
	}
	
	public static String safeTruncate(String str, int len)
	{
		if (str.length() <= len)
			return str;
		else
			return str.substring(0, len);
	}
	
	/**
	 * 
	 * @param str1
	 * @param str2
	 * @return 返回true，如果两个字符串相等；否则返回false。
	 */
	public static boolean equal(String str1,String str2) {
		if(str1 == null) {
			return str2 == null;
		} else {
			return str1.equals(str2);
		}
	}
	
	/**
	 * 将一个字符串进行标准化处理。
	 * 1、如果str是null，则设置成空串
	 * 2、如果str包含左右空格，则去掉。
	 * @return 返回一个标准化处理后的字符串。
	 */
	public static String normalize(String str) {
		if(str == null) {
			return EMPTY;
		} else if(str.length() == 0) {
			return EMPTY;
		} else {
			str = str.trim();
			if(str.length() == 0) {
				return EMPTY;
			} else {
				return str;
			}
		}
	}
	
	/** 
	    * 字符串截取，超出加“...”，要尽可能不浪费长度
	    * <param name="StrSource">原串</param>
	    * <param name="blackLength">截取长度</param>
	    * <returns>截取结果</returns>
	    */
	    public static String blockString(String StrSource, int blackLength)
	    {
	        if (isNullOrEmpty(StrSource)) return StrSource;
	        char[] charArray = StrSource.toCharArray();
	        int cutLength = 0;
	        String append = "…";
	        int i = 0;
	        for (; i < charArray.length; i++)
	        {
	            if (cutLength >= blackLength)//先预测一下
	                break;
	            if ((int)charArray[i] > 128)
	                cutLength += 2;//中文
	            else
	                cutLength += 1;
	            if (cutLength > blackLength)//在判断一下，有可能差1，加2
	                break;
	        }
	        StringBuilder sb = new StringBuilder(StrSource.substring(0, i));
	        if (sb.toString().length() < StrSource.length())//表示有截取，如果直接加上"…"，会超长
	        {
	            int pos = cutLength - blackLength;//当cutLength+1==blackLength的位置为中文字符时，正好多一位，所以这里的pos只有两种值0,1
	            if ((int)charArray[i - 1] > 128)
	                sb.delete(sb.length() - 1, sb.length());//如果最后的字符是中文，移除一位即可满足“…”的长度
	            else//如果是英文需要移除两个字符
	            {
	                if (pos > 0)
	                    sb.delete(sb.length() - 1, sb.length());//原来少一位，现在只需移除一个即可
	                else
	                    sb.delete(sb.length() - 2, sb.length());
	            }
	            sb.append(append);
	        }
	        return sb.toString();
	     }
}
