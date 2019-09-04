package com.feinno.superpojo;

import com.google.gson.JsonObject;

/**
 * 用于Json的序列及反序列接口
 * 
 * @author lvmingwei
 * 
 */
public interface IJsonPojo {

	public JsonObject toJsonObject();

//	public void parseJsonFrom(String json);

}
