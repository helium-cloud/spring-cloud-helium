package org.helium.redis.widgets.redis.client;

/**
 * Created by yibo on 2017-6-9.
 */
public class DbAndTable {


    private String dbName;
    private String tableName;


    public DbAndTable(String dbName, String tableName) {
        this.dbName = dbName;
        this.tableName = tableName;

    }

    public String getDbName() {
        return dbName;
    }

    public String getTableName() {
        return tableName;
    }
}
