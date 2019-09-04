package org.helium.framework.configuration;

import org.helium.framework.entitys.SetterNode;

import java.lang.reflect.Field;

/**
 *
 * @see org.helium.framework.annotations.FieldSetter
 * Created by Coral on 5/5/15.
 */
public interface FieldLoader {
	/**
	 * 按照field的反射类型初始化Setter
	 * @param node
	 * @return
	 */
	Object loadField(SetterNode node);

	/**
	 * 按照field的反射类型初始化Setter
	 * @param node
	 * @return
	 */
	default Object loadField(SetterNode node, Field field) {
		return loadField(node);
	}

	/**
	 * 用于在FieldSetter标注中表示空值
	 * @see FieldLoader
	 * @see org.helium.framework.annotations.FieldSetter
	 * Created by Coral on 7/5/15.
	 */
	final class Null implements FieldLoader {
		private Null() {
		}
		@Override
		public Object loadField(SetterNode node) {
			return null;
		}
	}
}
