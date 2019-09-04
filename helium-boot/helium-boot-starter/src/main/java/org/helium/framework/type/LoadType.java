package org.helium.framework.type;

import com.feinno.superpojo.type.EnumInteger;

public enum LoadType implements EnumInteger {
	XML(1), JSON(2), YML(3), PROPERTIES(4);

	int value;

	LoadType(int value) {
		this.value = value;
	}

	@Override
	public int intValue() {
		return value;
	}
}
