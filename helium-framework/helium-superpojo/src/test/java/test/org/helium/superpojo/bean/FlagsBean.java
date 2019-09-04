package test.org.helium.superpojo.bean;

import test.org.helium.superpojo.UserInfo.SexEnum;

import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.type.Flags;

public class FlagsBean {

	@Field(id = 1)
	private Flags<SexEnum> flags;

	public Flags<SexEnum> getFlags() {
		return flags;
	}

	public void setFlags(Flags<SexEnum> flags) {
		this.flags = flags;
	}

}
