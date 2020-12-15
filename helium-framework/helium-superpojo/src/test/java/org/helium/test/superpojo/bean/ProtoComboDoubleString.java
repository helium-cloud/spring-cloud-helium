/*
 * FAE, Feinno App Engine
 *  
 * Create by Fred 2011-4-17
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.test.superpojo.bean;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

/**
 * {在这里补充类的功能说明}
 * 
 * @auther Fred
 */
public class ProtoComboDoubleString extends SuperPojo {
	@Field(id = 1)
	private String str1;
	@Field(id = 2)
	private String str2;

	public ProtoComboDoubleString() {
	}

	public ProtoComboDoubleString(String v1, String v2) {
		this.str1 = v1;
		this.str2 = v2;
	}

	@Override
	public boolean equals(Object obj) {

		ProtoComboDoubleString rval = (ProtoComboDoubleString) obj;
		if (obj == null) {
			return false;
		} else {
			return this.str1.equals(rval.str1) && str2.equals(rval.str2);
		}
	}

	@Override
	public int hashCode() {
		return str1.hashCode() ^ str2.hashCode();
	}

	public String getStr1() {
		return str1;
	}

	public void setStr1(String str1) {
		this.str1 = str1;
	}

	public String getStr2() {
		return str2;
	}

	public void setStr2(String str2) {
		this.str2 = str2;
	}

}
