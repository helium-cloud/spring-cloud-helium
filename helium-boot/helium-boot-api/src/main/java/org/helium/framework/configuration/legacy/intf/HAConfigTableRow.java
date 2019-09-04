package org.helium.framework.configuration.legacy.intf;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

import java.util.List;

public class HAConfigTableRow extends SuperPojo
{
	@Field(id = 1)
	private List<String> values;

	public List<String> getValues()
	{
		return values;
	}

	public void setValues(List<String> values)
	{
		this.values = values;
	}

	public String getValue(int i)
	{
		return values.get(i);
	}
}
