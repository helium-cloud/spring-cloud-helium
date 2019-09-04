package org.helium.database.pooled;

import org.helium.data.sharding.ShardedDataSource;
import org.helium.database.Database;
import org.helium.framework.annotations.FieldLoaderType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by john.y on 2017-8-30.
 */


@FieldLoaderType(loaderType = PooledDatabaseLoader.class)
public class PooledDatabase extends ShardedDataSource<PooledObject, Database> {


    private ConcurrentHashMap<Integer, Database> dbMap = new ConcurrentHashMap<Integer, Database>();


    public ConcurrentHashMap<Integer, Database> getDbMap() {
        return dbMap;
    }

    public void setDbMap(ConcurrentHashMap<Integer, Database> dbMap) {
        this.dbMap = dbMap;
    }

    @Override
    protected Database loadDataSource(String dsName) {
        return null;
    }


    @Override
    public Database getSharding(PooledObject shardingKey) {
        if (shardingKey == null) {
            throw new IllegalArgumentException("null ty key");
        }

        int logicalPool = shardingKey.getLogicalPool();

        int physicalPool = logicalPool2PhysicalPool(logicalPool);

        return dbMap.get(physicalPool);
    }

    public int getPoolSize() {
        return dbMap.size();
    }

    public List<Integer> getAllPools() {
        List<Integer> result = new ArrayList<>();

        for (Integer item : dbMap.keySet()) {
            result.add(item);
        }

        return result;
    }

    private int logicalPool2PhysicalPool(int logicalPool) {
        //简单化,逻辑pool,就等于物理pool
        return logicalPool;
    }


    @Override
    protected Database getSharding(Database db, String shardingName) {
        throw new UnsupportedOperationException("BY DESIGN");
    }
}
