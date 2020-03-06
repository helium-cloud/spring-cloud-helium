package org.helium.test.superpojo.bean;

import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.type.Flags;
import org.helium.test.superpojo.UserInfo;

public class FlagsBean {

	@Field(id = 1)
	private Flags<UserInfo.SexEnum> flags;

	public Flags<UserInfo.SexEnum> getFlags() {
		return flags;
	}

	public void setFlags(Flags<UserInfo.SexEnum> flags) {
		this.flags = flags;
	}

}
