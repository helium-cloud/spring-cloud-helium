package org.helium.framework.configuration.loaders;

import org.helium.framework.BeanContext;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.KeyValueNode;
import org.helium.framework.entitys.MapConfiguration;
import org.helium.framework.entitys.SetterNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Coral on 10/15/15.
 */
public class StringMapLoader implements FieldLoader {
	@Override
	public Object loadField(SetterNode node) {
		ConfigProvider configProvider = BeanContext.getContextService().getService(ConfigProvider.class);
		MapConfiguration map = configProvider.loadXml(node.getInnerText(), MapConfiguration.class);
		Map<String, String> result = new HashMap<>();
		for (KeyValueNode n: map.getEntrys()) {
			result.put(n.getKey(), n.getValue());
		}
		return result;
	}
}
