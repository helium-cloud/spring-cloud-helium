/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2012-2-15
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

/**
 * Rpc包体扩展字段
 * <p>
 * Created by Coral
 */
public class RpcBodyExtension extends SuperPojo {
	/**
	 * 扩展字段序号
	 */
	@Field(id = 1, isRequired = true)
	private int id;

	/**
	 * 扩展字段长度，
	 */
	@Field(id = 2, isRequired = true)
	private int length;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
}
