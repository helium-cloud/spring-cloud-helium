package org.helium.redis.widgets.redis.client;

/**
 * Created by yibo on 2017-6-9.
 */
public class DbTableRole {

    private String dbName;
    private String tableName;
    private String roleName;

    public DbTableRole(String dbName, String tableName, String roleName) {
        this.dbName = dbName;
        this.tableName = tableName;
        this.roleName = roleName;
    }

    public DbTableRole(String dbName, String tableName) {
        this.dbName = dbName;
        this.tableName = tableName;

    }


    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
