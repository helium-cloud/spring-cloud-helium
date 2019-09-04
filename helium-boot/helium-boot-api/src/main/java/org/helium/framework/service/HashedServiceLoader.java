package org.helium.framework.service;

import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.SetterNode;

import java.lang.reflect.Field;

/**
 * Created by Coral on 9/10/15.
 */
public class HashedServiceLoader implements FieldLoader {
	@Override
	public Object loadField(SetterNode node) {
		return null;
	}

	@Override
	public Object loadField(SetterNode node, Field field) {
		field.getType().getTypeParameters();
		throw new UnsupportedOperationException("NotImplemented");
	}
}
