package org.helium.data.zookeeper.utils;

import org.apache.zookeeper.data.Stat;
import org.helium.data.zookeeper.configCenter.manager.ZKConfigurationManager;
import org.helium.data.zookeeper.configCenter.watcher.DBConnectionStringWatcher;
import org.helium.database.ConnectionString;
import org.helium.database.Database;
import org.helium.database.spi.DatabaseManager;
import org.helium.framework.configuration.Environments;
import org.helium.framework.route.zk.ZooKeeperConnector;
import zconfig.args.ConfigTextArgs;

import java.io.IOException;

/**
 * Created by liufeng on 2017/8/21.
 */
public class CommonUtils {
    public static Database getDatabase(String nodePath, String databaseName, ZooKeeperConnector zkConnector)
    {
        try {
            Stat stat2 = new Stat();
            byte[] buffer2 = zkConnector.getZookeeper().getData(nodePath, new DBConnectionStringWatcher(zkConnector), stat2);

            ConfigTextArgs configTextArgs = new ConfigTextArgs();
            configTextArgs.parsePbFrom(buffer2);

            String configText = configTextArgs.getConfigText();

            String dbTxt = Environments.applyConfigText(databaseName, configText);

            ConnectionString connStr;
            try {
                connStr = ConnectionString.fromText(dbTxt);
            } catch (IOException e) {
                throw new IllegalArgumentException("Bad Connection String:" + dbTxt);
            }

            if (connStr == null) {
                return null;
            }

            Database db = DatabaseManager.INSTANCE.getDatabase(databaseName, connStr);

            if (db != null) {
                ZKConfigurationManager.getInstance().putDatabase(databaseName, db);
            }

            return db;
        }
        catch (Exception ex)
        {
            return  null;
        }
    }
}
