package zconfig.args;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

/**
 * Created by liufeng on 2017/8/10.
 */
public class ConfigTableArgs extends SuperPojo {
    @Field(id = 1)
    private String tableName;

    @Field(id = 2)
    private String databaseName;

    @Field(id = 3)
    private String version;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
