package org.helium.database.sharding;

import org.helium.data.sharding.ShardedDataSource;
import org.helium.database.Database;
import org.helium.database.spi.DatabaseManager;

/**
 * Created by Coral on 7/25/16.
 */
public class ShardedDatabase<SK> extends ShardedDataSource<SK, Database> {
	public ShardedDatabase() {
	}

	@Override
	protected Database loadDataSource(String dsName) {
		return DatabaseManager.INSTANCE.getDatabase(dsName);
	}

	@Override
	protected Database getSharding(Database db, String shardingName) {
		return new DatabaseSharding(db, shardingName);
	}
}
