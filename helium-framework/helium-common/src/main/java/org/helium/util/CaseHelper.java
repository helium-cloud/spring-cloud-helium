package org.helium.util;

/**
 * 支持字符串全半角功能的转换
 * 当年从C#无脑移植过来的类，不建议直接使用
 * Created by Coral on 8/26/15.
 */
public class CaseHelper {

	/// <summary>
	/// 检查前len位是不是全半角
	/// </summary>
	/// <param name="str"></param>
	/// <param name="len"></param>
	/// <returns></returns>
	public static boolean isAllDBC(String str, int len) {
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
	public static boolean isAllDBC(String str) {
		boolean rt = true;
		for (int i = 0; i < str.length(); i++)
			if (str.charAt(i) > 0x00FE)
				return false;
		return rt;
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
	public static String toDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
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
	/// 截取字符串，全角长度算2个
	/// </summary>
	/// <param name="source"></param>
	/// <param name="length"></param>
	/// <returns></returns>
	public static String truncateStringWithDBC(String source, int length) {
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
	/// <summary>
	/// 一个字符转半角
	/// </summary>
	/// <param name="a"></param>
	/// <returns></returns>
	public static char toDBC(char a) {
		if ((a >= '!' && a <= '~'))
			return a;
		else if ((a >= '！' && a <= '～'))
			return (char) ((a & 0x00ff) + 0x0020);
		else
			return a;
	}

	/// <summary>
	/// 一个字符转全角
	/// </summary>
	/// <param name="a"></param>
	/// <returns></returns>
	public static char toSBC(char a) {
		if ((a >= '！' && a <= '～'))
			return a;
		else if ((a >= '!' && a <= '~'))
			return (char) ((a - 0x0020) | 0xff00);
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
	public static String toSBC(String input) {
		//半角转全角：
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 32) {
				c[i] = (char) 12288;
				continue;
			}
			if (c[i] < 127)
				c[i] = (char) (c[i] + 65248);
		}
		return new String(c);
	}

	public static int str2int(String str, int def) {
		try {
			if (str.length() == 0)
				return def;
			str = toDBC(str);
			return Integer.parseInt(str);
		} catch (Exception e) {

			return def;
		}

	}

	public static long str2int(String str, long def) {

		try {
			if (str.length() == 0)
				return def;
			str = toDBC(str);
			return Long.parseLong(str);
		} catch (Exception e) {

			return def;
		}
	}
}
