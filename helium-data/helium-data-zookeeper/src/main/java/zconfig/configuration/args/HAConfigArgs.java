package zconfig.configuration.args;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

import java.util.Date;

/**
 * 
 * 用于所有配置接口的HAConfigArgs 1.
 * 
 * @author 高磊 gaolei@feinno.com
 */
public class HAConfigArgs extends SuperPojo
{
	@Field(id = 1)
	private ConfigType type;

	@Field(id = 2)
	private String path;

	@Field(id = 3)
	private String params;

	@Field(id = 4)
	private Date version;

	public ConfigType getType()
	{
		return type;
	}

	public void setType(ConfigType type)
	{
		this.type = type;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public void setParams(String params)
	{
		this.params = params;
	}

	public String getParams()
	{
		return this.params;
	}

	public Date getVersion()
	{
		return version;
	}

	public void setVersion(Date version)
	{
		this.version = version;
	}
}
