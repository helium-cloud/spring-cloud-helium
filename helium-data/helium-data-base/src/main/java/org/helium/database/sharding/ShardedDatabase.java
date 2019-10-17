package org.helium.database.sharding;

import org.helium.data.sharding.ShardedDataSource;
import org.helium.database.ConnectionString;
import org.helium.database.Database;
import org.helium.database.spi.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Coral on 7/25/16.
 */
public class ShardedDatabase<SK> extends ShardedDataSource<SK, Database> {
    private static final Logger logger = LoggerFactory.getLogger(ShardedDatabase.class);
	public ShardedDatabase() {
	}

	@Override
	protected Database loadDataSource(String dsName) {
		return DatabaseManager.INSTANCE.getDatabase(dsName);
	}

    @Override
    protected Database loadDataSource(String dsName, String value) {
        ConnectionString connectionString = null;
        try {
            connectionString = ConnectionString.fromText(value.replace(" ","\n"));
        } catch (IOException e) {
            logger.error("ShardedDatabase is error",e);
        }
        return DatabaseManager.INSTANCE.getDatabase(dsName, connectionString);
    }

    @Override
	protected Database getSharding(Database db, String shardingName) {
		return new DatabaseSharding(db, shardingName);
	}
}
