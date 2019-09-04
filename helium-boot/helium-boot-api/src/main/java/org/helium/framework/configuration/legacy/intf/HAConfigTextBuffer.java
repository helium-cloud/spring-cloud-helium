/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2011-1-20
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.framework.configuration.legacy.intf;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

import java.util.Date;
import java.util.List;

/**
 * 配置文本
 * 
 * @author 高磊 gaolei@feinno.com
 */
public class HAConfigTextBuffer extends SuperPojo
{
	@Field(id = 1)
	private String text;

	@Field(id = 2)
	private List<String> configParams;

	@Field(id = 3)
	private Date version;

	public HAConfigTextBuffer()
	{
	}

	public HAConfigTextBuffer(String text)
	{
		this.text = text;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public void setConfigParams(List<String> configParams)
	{
		this.configParams = configParams;
	}

	public List<String> getConfigParams()
	{
		return configParams;
	}

	public Date getVersion() {
		return version;
	}

	public void setVersion(Date version) {
		this.version = version;
	}
	
}
