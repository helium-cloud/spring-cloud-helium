package org.helium.framework.entitys;

import com.feinno.superpojo.type.EnumInteger;

public enum SetterNodeLoadType implements EnumInteger {
	UNKNOWN(1),
	CONFIG_PROVIDE(2),
	CONFIG_VALUE(3),
	CONFIG_DYNAMIC(4),
	;
	private int value;

	SetterNodeLoadType(int value) {
		this.value = value;
	}
	public int intValue() {
		return value;
	}

}
