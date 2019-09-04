/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2011-3-23
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.util;

/**
 * 请改用<code>StringUtils</code>
 * @see StringUtils
 * Created by Coral
 */
@Deprecated
public class StrUtils {
	/**
	 * 判断是否null或""
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(String str) {
		return str == null ? true : str.equals("");
	}
}
