package org.helium.framework.configuration.loaders;

import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.SetterNode;

/**
 * Created by Coral on 10/12/15.
 */
public class StringPackageLoader implements FieldLoader {
	@Override
	public Object loadField(SetterNode node) {
		String file = node.getInnerText();
		return new StringPackageImpl(file);
	}
}
