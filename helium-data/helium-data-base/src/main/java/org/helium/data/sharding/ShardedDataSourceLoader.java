package org.helium.data.sharding;

import org.helium.data.sharding.configuration.DataSourceNode;
import org.helium.data.sharding.configuration.ShardedDataSourceConfiguration;
import org.helium.framework.BeanContext;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.ObjectWithSettersNode;
import org.helium.framework.entitys.SetterNode;
import org.helium.framework.spi.ObjectCreator;

import java.lang.reflect.Field;

/**
 * Created by Coral on 7/22/16.
 */
public class ShardedDataSourceLoader implements FieldLoader {
	@Override
	public Object loadField(SetterNode node) {
		throw new UnsupportedOperationException("Should not supported");
	}

	@Override
	public Object loadField(SetterNode node, Field field) {
		ShardedDataSource ds;
		String shardedFile = node.getInnerText();
		ConfigProvider provider = BeanContext.getContextService().getService(ConfigProvider.class);

		ShardedDataSourceConfiguration config = provider.loadXml("db/" + shardedFile, ShardedDataSourceConfiguration.class);

		try {
			Object o = field.getType().newInstance();
			ds = (ShardedDataSource)o;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		for (DataSourceNode n: config.getDataSources()) {
			ds.addDataSource(n.getId(), n.getName());
		}

		ObjectWithSettersNode sn = config.getShardingFunction();
		Object o2 = ObjectCreator.createObject(sn);
		ds.setShardingFunction((ShardingFunction)o2);
		return ds;
	}
}
