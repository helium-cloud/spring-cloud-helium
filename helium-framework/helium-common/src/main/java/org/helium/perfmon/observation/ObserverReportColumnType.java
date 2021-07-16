package org.helium.perfmon.observation;

import org.helium.superpojo.type.EnumInteger;

/**
 * 输出对象类型
 *
 * Created by Coral
 */
public enum ObserverReportColumnType implements EnumInteger {
	LONG(0, "long") {
		@Override
		public Object parse(String valueStr) {
			if (valueStr == null || valueStr.isEmpty()) {
				return null;
			} else {
				return Long.parseLong(valueStr);
			}
		}
	},
	DOUBLE(1, "double") {
		@Override
		public Object parse(String valueStr) {
			if (valueStr == null || valueStr.isEmpty()) {
				return null;
			} else {
				return Double.parseDouble(valueStr);
			}
		}
	},
	RATIO(2, "double") {
		@Override
		public Object parse(String valueStr) {
			if (valueStr == null || valueStr.isEmpty()) {
				return null;
			} else {
				return Double.parseDouble(valueStr);
			}
		}
	},
	TEXT(3, "String");

	ObserverReportColumnType(int value, String originalType) {
		this.value = value;
		this.originalType = originalType;
	}

	@Override
	public int intValue() {
		return value;
	}

	private int value;

	private String originalType;

	public Object parse(String valueStr) {
		return valueStr;
	}

	public String getOriginalType() {
		return originalType;
	}
}
