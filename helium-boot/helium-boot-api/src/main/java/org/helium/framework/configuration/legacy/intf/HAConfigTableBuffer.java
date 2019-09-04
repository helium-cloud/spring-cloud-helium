package org.helium.framework.configuration.legacy.intf;

import com.feinno.superpojo.SuperPojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 保存ConfigTable数据的序列化缓冲类
 * 
 * @author 高磊 gaolei@feinno.com
 */
public class HAConfigTableBuffer extends SuperPojo
{
	@com.feinno.superpojo.annotation.Field(id = 1)
	private String tableName;

	@com.feinno.superpojo.annotation.Field(id = 2)
	private Date version;

	@com.feinno.superpojo.annotation.Field(id = 3)
	private List<String> columns = new ArrayList<String>();

	@com.feinno.superpojo.annotation.Field(id = 4)
	private List<HAConfigTableRow> rows = new ArrayList<HAConfigTableRow>();

	@com.feinno.superpojo.annotation.Field(id = 5)
	private long dynamicTableVersion;

	public long getDynamicTableVersion()
	{
		return dynamicTableVersion;
	}

	public void setDynamicTableVersion(long dynamicTableVersion)
	{
		this.dynamicTableVersion = dynamicTableVersion;
	}

	public Date getVersion()
	{
		return version;
	}

	public void setVersion(Date version)
	{
		this.version = version;
	}

	public List<String> getColumns()
	{
		return columns;
	}

	public void setColumns(List<String> columns)
	{
		this.columns = columns;
	}

	public List<HAConfigTableRow> getRows()
	{
		return rows;
	}

	public void setRows(List<HAConfigTableRow> rows)
	{
		this.rows = rows;
	}

	public int rowCount()
	{
		return rows.size();
	}

	public HAConfigTableRow getRow(int i)
	{
		return rows.get(i);
	}

	public String getTableName()
	{
		return tableName;
	}

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}


}
