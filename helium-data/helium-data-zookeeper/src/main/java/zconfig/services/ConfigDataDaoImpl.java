package zconfig.services;

import org.helium.database.DataRow;
import org.helium.database.DataTable;
import org.helium.database.Database;
import org.helium.framework.annotations.FieldSetter;
import org.helium.framework.annotations.ServiceImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zconfig.api.ConfigDataDao;
import zconfig.args.ConfigTableArgs;
import zconfig.args.ConfigTextArgs;
import zconfig.args.ConfigUpdateNotifyArgs;
import zconfig.utils.ConfigUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liufeng on 2017/8/14.
 */
@ServiceImplementation
public class ConfigDataDaoImpl implements ConfigDataDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigDataDaoImpl.class);

    @FieldSetter("${URCS_ZKDB}")
    private Database database;

    public ConfigDataDaoImpl()
    {
        int a = 20;
    }

    @Override
    public List<ConfigTableArgs> loadConfigTable()
    {
        try {
            String sql = "SELECT * FROM URCS_ConfigTable";

            DataTable dt = database.executeTable(sql);

            List<ConfigTableArgs> configTableList = null;
            ConfigTableArgs configTableArgs = null;

            if (dt != null && dt.getRowCount() > 0) {
                configTableList = new ArrayList<ConfigTableArgs>();

                for (DataRow row : dt.getRows()) {
                    configTableArgs = new ConfigTableArgs();

                    configTableArgs.setTableName(row.getString("TableName"));
                    configTableArgs.setDatabaseName(row.getString("DatabaseName"));
                    configTableArgs.setVersion(ConfigUtils.convertDateToString(row.getDateTime("Version")));

                    configTableList.add(configTableArgs);
                }
            }

            return configTableList;
        }
        catch (Exception ex)
        {
            LOGGER.error(String.format("loadConfigTable is error, %s", ex.getMessage()), ex);

            return null;
        }
    }

    @Override
    public List<ConfigTextArgs> loadConfigText()
    {
        try {
            String sql = "SELECT * FROM URCS_ConfigText";

            DataTable dt = database.executeTable(sql);

            List<ConfigTextArgs> configTextList = null;
            ConfigTextArgs configTextArgs = null;

            if (dt != null && dt.getRowCount() > 0) {
                configTextList = new ArrayList<ConfigTextArgs>();

                for (DataRow row : dt.getRows()) {
                    configTextArgs = new ConfigTextArgs();

                    configTextArgs.setConfigKey(row.getString("ConfigKey"));
                    configTextArgs.setConfigText(row.getString("ConfigText"));
                    configTextArgs.setVersion(ConfigUtils.convertDateToString(row.getDateTime("Version")));

                    configTextList.add(configTextArgs);
                }
            }

            return configTextList;
        }
        catch (Exception ex)
        {
            LOGGER.error(String.format("loadConfigText is error, %s", ex.getMessage()), ex);
            return null;
        }
    }

    @Override
    public void insertConfigUpdateNotify(ConfigUpdateNotifyArgs args)
    {
        try {
            String insertSql = "INSERT INTO URCS_ConfigUpdateNotify(`ConfigKey`,`ConfigType`,`LoadDataMode`, `ServiceName`,`MachineAddress`,`LastReadTime`) VALUES(?, ?, ?, ?, ?, ?)";

            database.executeInsert(insertSql,
                    args.getConfigKey(),
                    args.getConfigType().toString().toLowerCase(),
                    args.getLoadDataMode().getName(),
                    args.getServiceName(),
                    args.getMachineAddress(),
                    args.getLastReadTime());
        } catch (Exception ex) {
            LOGGER.error(String.format("insertConfigUpdateNotify error, %s", ex.getMessage()), ex);
        }
    }

    @Override
    public void updateConfigTableArgs(ConfigTableArgs configTableArgs) {
        try{
        String updateSql="update  URCS_ConfigTable set  DatabaseName=?,Version=? where TableName=?";
        database.executeUpdate(updateSql,configTableArgs.getDatabaseName(),configTableArgs.getVersion(),configTableArgs.getTableName());
        }catch (Exception ex){
            LOGGER.error(String.format("updateConfigTableArgs error, %s", ex.getMessage()), ex);
        }
    }

    @Override
    public void updateConfigTextArgs(ConfigTextArgs configTextArgs)  {
        String updateSql="update URCS_ConfigText set ConfigText=?,Version=? where ConfigKey=?";
        try {
            database.executeUpdate(updateSql,configTextArgs.getConfigText(), configTextArgs.getVersion(),configTextArgs.getConfigKey());
        }catch (Exception ex){
            LOGGER.error(String.format("updateConfigTextArgs error, %s", ex.getMessage()), ex);
        }

    }

    @Override
    public ConfigTableArgs selectConfigTableArgs(String tableName) {
        String selectSql="select * from URCS_ConfigTable where TableName=?";
        ConfigTableArgs configTableArgs=null;
        try {
            DataTable dataTable = database.executeTable(selectSql, tableName);

            if (dataTable.getRowCount() > 0) {
                configTableArgs = new ConfigTableArgs();
                DataRow dataRow = dataTable.getRow(0);
                configTableArgs.setVersion(dataRow.getString("Version"));
                configTableArgs.setTableName(dataRow.getString("TableName"));
                configTableArgs.setDatabaseName(dataRow.getString(" DatabaseName"));
            }
        }catch (Exception ex){
            LOGGER.error(String.format("selectConfigTableArgs error, %s", ex.getMessage()), ex);
        }
        return configTableArgs;
    }

    @Override
    public ConfigTextArgs selectConfigTextArgs(String tableName) {
       String sql="select * from URCS_ConfigText where ConfigKey=?";
        ConfigTextArgs configTextArgs=null;
        try {
            DataTable dataTable = database.executeTable(sql, tableName);
            if (dataTable.getRowCount() >= 0) {
                DataRow dataRow = dataTable.getRow(0);
                configTextArgs = new ConfigTextArgs();
                configTextArgs.setVersion(dataRow.getString("Version"));
                configTextArgs.setConfigKey(dataRow.getString("ConfigKey"));
                configTextArgs.setConfigText(dataRow.getString("ConfigText"));
            }
        }catch (Exception ex){
            LOGGER.error(String.format("selectConfigTextArgs error, %s", ex.getMessage()), ex);
        }
        return configTextArgs;
    }
}
