package org.helium.framework;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Coral on 7/2/15.
 */
public enum BeanType {
	CONFIGURATOR("Configurator"),
	SERVICE("Service"),
	SERVLET("Servlet"),
	TASK("Task"),
	MODULE("Module"),
	;
	private String value;
	BeanType(String value) {
		this.value = value;
	}

	public String strValue() {
		return value;
	}

	private static Map<String, BeanType> types;

	public static BeanType fromText(String value) {
		if (types == null) {
			types = new HashMap<>();
			for (BeanType e: BeanType.values()) {
				types.put(e.strValue().toUpperCase(), e);
			}
		}
		BeanType r = types.get(value.toUpperCase());
		if (r == null) {
			throw new IllegalArgumentException("Unknown BeanType:" + value);
		}
		return r;
	}
}