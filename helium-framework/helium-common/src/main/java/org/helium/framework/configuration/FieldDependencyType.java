package org.helium.framework.configuration;


import org.helium.superpojo.type.EnumInteger;

/**
 * Bean的依赖类型
 * Created by Coral on 7/8/15.
 */
public enum FieldDependencyType implements EnumInteger {
	SERVICE(1),
	TASK(2),
	CONFIG_VARIABLE(3),
	LOADER_VARIABLE(4),
	;
	private int value;

	FieldDependencyType(int value) {
		this.value = value;
	}
	public int intValue() {
		return value;
	}
}
