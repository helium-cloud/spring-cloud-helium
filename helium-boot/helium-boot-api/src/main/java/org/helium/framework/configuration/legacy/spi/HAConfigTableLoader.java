package org.helium.framework.configuration.legacy.spi;

import com.feinno.superpojo.type.Flags;
import com.feinno.superpojo.util.EnumParser;
import org.helium.framework.configuration.legacy.*;
import org.helium.framework.configuration.legacy.intf.HAConfigTableBuffer;
import org.helium.framework.configuration.legacy.intf.HAConfigTableRow;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Hashtable;

/**
 * Created by Leon on 8/3/16.
 */
public class HAConfigTableLoader {
	/**
	 *
	 * 将缓冲区转换为最终可用的Hashtable
	 *
	 * @param <K>
	 * @param <V>
	 * @param keyType
	 * @param valueType
	 * @return
	 * @throws ConfigurationNotFoundException
	 * @throws ConfigurationFailedException
	 */
	@SuppressWarnings("unchecked")
	public static <K, V extends ConfigTableItem> ConfigTable<K, V> fromBuffer(HAConfigTableBuffer buffer, Class<K> keyType, Class<V> valueType) throws ConfigurationNotFoundException, ConfigurationFailedException
	{
		int columnCount = buffer.getColumns().size();
		// int keyCount = keyType.getDeclaredFields().length;
		Hashtable<K, V> innerTable = new Hashtable<K, V>();
		boolean simpleKey = !(keyType.getSuperclass().equals(ConfigTableKey.class));

		if (simpleKey) {
			// TODO 增加简单类型检查
			// if (!keyType.isPrimitive() && !keyType.equals(String.class)) {
			// throw new
			// IllegalArgumentException("No-simple type must extends from ConfigTableKey:"
			// + keyType.getName());
			// }
		}
		Field[] valueFields = new Field[columnCount];
		ConfigTableField[] valueAttrs = new ConfigTableField[columnCount];

		ConfigTableField[] keyAttrs = new ConfigTableField[columnCount];
		Field[] keyFields = new Field[columnCount];

		Field[] fields = valueType.getDeclaredFields();
		for (Field field : fields) {
			ConfigTableField attr = AnnotationHelper.tryGetAnnotation(ConfigTableField.class, field);
			// boolean find = false;
			if (attr != null) {
				for (int i = 0; i < columnCount; i++) {
					if (buffer.getColumns().get(i).equals(attr.value())) {
						// find = true;
						valueFields[i] = field;
						valueAttrs[i] = attr;
						if (simpleKey && attr.isKeyField()) {
							keyFields[i] = field;
						}
						break;
					}
				}
			}
			/*
			 * if (!find && attr.required()) { throw new
			 * ConfigurationNotFoundException(getTableName(), "", attr.value());
			 * }
			 */
		}
		if (!simpleKey) {
			fields = keyType.getDeclaredFields();
			for (Field field : fields) {
				ConfigTableField attr = AnnotationHelper.tryGetAnnotation(ConfigTableField.class, field);
				boolean find = false;
				if (attr != null) {
					for (int i = 0; i < columnCount; i++) {
						if (buffer.getColumns().get(i).equals(attr.value())) {
							keyFields[i] = field;
							keyAttrs[i] = attr;
							find = true;
							break;
						}
					}
					if (!find) {
						throw new ConfigurationNotFoundException(ConfigType.TABLE, buffer.getTableName(), attr.value());
					}
				}
			}
		}
		if (buffer.getRows() != null) // 这都不能兼容，小心
		{
			int index = 0;

			for (HAConfigTableRow row : buffer.getRows()) {
				try {
					Object key = null;
					Object value;

					index++;

					value = valueType.newInstance();

					if (!simpleKey)
						key = keyType.newInstance();

					for (int i = 0; i < columnCount; i++) {
						if (valueFields[i] == null)
							continue;

						String valStr = row.getValue(i);
						if (valueAttrs[i].trim() && valStr != null)
							valStr = valStr.trim();
						Class<?> clazz = valueFields[i].getType();
						Object fieldValue = null;
						if (clazz.equals(Flags.class)) {
							try {
								// int n = Integer.parseInt(valStr);
								fieldValue = EnumParser.parseFlags(clazz, valStr, true);
							} catch (Exception e) {
								ParameterizedType pt = (ParameterizedType) valueFields[i].getGenericType();
								Type[] aType = pt.getActualTypeArguments();
								String s = aType[0].toString().split(" ")[1];
								Class<?> genericClass = Class.forName(s);
								fieldValue = EnumParser.parseFlags(genericClass, valStr, true);
							}
						} else
							fieldValue = ObjectHelper.convertTo(valStr, clazz);
						// Object fieldValue = null;
						valueFields[i].setAccessible(true);
						valueFields[i].set(value, fieldValue);
						if (keyFields[i] != null) {
							if (simpleKey) {
								key = fieldValue;
							} else {
								keyFields[i].setAccessible(true);
								keyFields[i].set(key, fieldValue);
							}
						}
					}
					K k1 = (K) key;
					V v1 = (V) value;
					innerTable.put(k1, v1);
				} catch (Exception ex) {
					throw new ConfigurationFailedException(ConfigType.TABLE, buffer.getTableName(), ex);
				}
			}
		}

		ConfigTable<K, V> table = new ConfigTable<K, V>(buffer.getTableName(), innerTable, buffer.getVersion(), keyType, valueType);
		try {
			table.runAfterLoad();
		} catch (Exception e) {
			throw new ConfigurationFailedException(ConfigType.TABLE,"runAfterLoad",e);
		}
		return table;
	}
}
