package org.helium.framework.type;

import com.feinno.superpojo.type.EnumInteger;

public enum LoadState implements EnumInteger {
	START(1), INIT(2), PROCESSING(3), WAIT(4), COMPLETE(5);

	int value;

	LoadState(int value) {
		this.value = value;
	}

	@Override
	public int intValue() {
		return value;
	}
}
