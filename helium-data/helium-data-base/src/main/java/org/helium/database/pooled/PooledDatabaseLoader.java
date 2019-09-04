package org.helium.database.pooled;

import org.helium.database.Database;
import org.helium.database.spi.DatabaseManager;
import org.helium.framework.BeanContext;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.SetterNode;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by john.y on 2017-8-30.
 */
public class PooledDatabaseLoader implements FieldLoader {

    @Override
    public Object loadField(SetterNode node) {

        PooledDatabase result = new PooledDatabase();
        String shardedFile = node.getInnerText();
        ConfigProvider provider = BeanContext.getContextService().getService(ConfigProvider.class);

        DataSourceConfiguration config = provider.loadXml( shardedFile, DataSourceConfiguration.class);

        ConcurrentHashMap<Integer, Database> dbs = new ConcurrentHashMap<>();

        result.setDbMap(dbs);

        for (DatabaseNode n : config.getDataSources()) {

            Database db = DatabaseManager.INSTANCE.getDatabase(n.getName());
            dbs.put(n.getId(), db);
        }

        return result;
    }
}
