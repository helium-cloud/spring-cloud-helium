package org.helium.framework.spi;

import org.helium.superpojo.SuperPojo;
import org.helium.framework.entitys.SetterNode;

import java.util.function.Function;

/**
 * Setter注入的fieldType
 */
enum SetterFieldType {
	STRING(String.class, s -> s),
	BOOLEAN(boolean.class, Boolean.class, s -> Boolean.parseBoolean(s)),
	BYTE(byte.class, Byte.class, s -> Byte.parseByte(s)),
	SHORT(short.class, Short.class, s -> Short.parseShort(s)),
	INTEGER(int.class, Integer.class, s -> Integer.parseInt(s)),
	LONG(long.class, Long.class, s -> Long.parseLong(s)),
	FLOAT(float.class,  Float.class, s -> Float.parseFloat(s)),
	DOUBLE(double.class, Double.class, s -> Double.parseDouble(s)),
	CHAR(char.class, Character.class) {
		@Override
		public Object convertFrom(Class<?> toClazz, String s) {
			if (s.length() == 1) {
				return s.charAt(0);
			} else {
				throw new IllegalArgumentException("expects char:" + s);
			}
		}
	},
	ENUM(Enum.class) {
		@Override
		public Object convertFrom(Class<?> toClazz, String s) {
			return Enum.valueOf((Class<Enum>) toClazz, s);
		}
	},
	SUPERPOJO(SuperPojo.class) {
		@Override
		public Object convertFrom(Class<?> toClazz, SetterNode node) {
			return null;
			//return node.getValue().convertTo((Class<SuperPojo>) toClazz);
		}
	};

	private Class<?>[] supportTypes;
	private Function<String, Object> parser;

	SetterFieldType(Class<?> clazz) {
		supportTypes = new Class<?>[] {clazz };
	}

	SetterFieldType(Class<?> c1, Class<?> c2) {
		supportTypes = new Class<?>[] {c1, c2};
	}

	SetterFieldType(Class<?> clazz, Function<String, Object> parser) {
		this(clazz);
		this.parser = parser;
	}

	SetterFieldType(Class<?> c1, Class<?> c2, Function<String, Object> parser) {
		this(c1, c2);
		this.parser = parser;
	}

	private boolean isSupport(Class<?> clazz) {
		if (supportTypes == null || supportTypes.length == 0) {
			return false;
		}
		for (Class<?> type : supportTypes) {
			if (type == clazz || type.equals(clazz) || type.isAssignableFrom(clazz)) {
				return true;
			}
		}
		return false;
	}

	public static SetterFieldType valueOf(Class<?> clazz) {
		for (SetterFieldType type : SetterFieldType.values()) {
			if (type.isSupport(clazz)) {
				return type;
			}
		}
		return null;
	}

	public Object convertFrom(Class<?> toClazz, String text) {
		if (parser != null) {
			return parser.apply(text);
		} else {
			throw new UnsupportedOperationException("Must override convertFrom() method");
		}
	}

	public Object convertFrom(Class<?> toClazz, SetterNode node) {
		return convertFrom(toClazz, node.getValue());
	}
}