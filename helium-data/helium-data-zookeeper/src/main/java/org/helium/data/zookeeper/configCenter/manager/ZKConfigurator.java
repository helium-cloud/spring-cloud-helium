package org.helium.data.zookeeper.configCenter.manager;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.helium.data.zookeeper.configCenter.entity.ZKConfigTable;
import org.helium.data.zookeeper.configCenter.entity.ZKConfigTableBuffer;
import org.helium.data.zookeeper.configCenter.watcher.ConfigTableWatcher;
import org.helium.data.zookeeper.configCenter.watcher.ConfigTextWatcher;
import org.helium.data.zookeeper.utils.CommonUtils;
import org.helium.database.DataRow;
import org.helium.database.DataTable;
import org.helium.database.Database;
import org.helium.framework.route.zk.ZooKeeperConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zconfig.ConstantKey;
import zconfig.args.ConfigTableArgs;
import zconfig.args.ConfigTextArgs;
import zconfig.args.SubscribeDataArgs;
import zconfig.configuration.args.ConfigTableItem;
import zconfig.configuration.args.ConfigType;
import zconfig.configuration.args.HAConfigTableRow;
import zconfig.utils.ConfigUtils;
import zconfig.utils.ZKConfigPathUtil;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liufeng on 2017/8/10.
 */
public class ZKConfigurator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZKConfigurator.class);

    private ZooKeeperConnector zkConnector;

    public ZKConfigurator(ZooKeeperConnector zkConnector) {
        this.zkConnector = zkConnector;
    }

    public ZooKeeperConnector getZkConnector() {
        return zkConnector;
    }


    public <K, V extends ConfigTableItem> ZKConfigTable<K, V> loadConfigTable(Class<K> keyType,
                                                                              Class<V> valueType,
                                                                              String path) throws Exception {
        String nodePath = ZKConfigPathUtil.getConfigChildTablePath(path);

        Stat stat = new Stat();
        byte[] buffer = zkConnector.getZookeeper().getData(nodePath, new ConfigTableWatcher(zkConnector), stat);

        ConfigTableArgs configTableArgs = new ConfigTableArgs();
        configTableArgs.parsePbFrom(buffer);

        Database db = ZKConfigurationManager.getInstance().getDatabase(configTableArgs.getDatabaseName());

        if (db == null) {
            String dbFileName = String.format("%s.properties", configTableArgs.getDatabaseName());

            nodePath = ConstantKey.ZK_CONFIG_CHILD_NOTE_TEXT_PATH + "/" + dbFileName;

            db = CommonUtils.getDatabase(nodePath, configTableArgs.getDatabaseName(), zkConnector);
        }

        String sql = String.format("select * from %s", configTableArgs.getTableName());

        DataTable dataTable = db.executeTable(sql);

        ZKConfigTableBuffer tableBuffer = new ZKConfigTableBuffer();

        tableBuffer.setTableName(configTableArgs.getTableName());
        tableBuffer.setVersion(ConfigUtils.convertStringToDate(configTableArgs.getVersion()));

        ZKConfigTableBuffer tableBufferCache = getConfigTable(dataTable, tableBuffer);

        ZKConfigTable<K, V> table = tableBufferCache.toTable(keyType, valueType);

        ZKConfigDataManager.putConfigTable(path, ConfigType.TABLE, table);

        return table;
    }

    private ZKConfigTableBuffer getConfigTable(DataTable table, ZKConfigTableBuffer tableBuffer)
            throws SQLException {
        List<String> cols = new ArrayList<String>(table.getColumnCount());
        for (int i = 0; i < table.getColumnCount(); i++) {
            cols.add(table.getColumn(i + 1).getColumnName());
        }
        tableBuffer.setColumns(cols);
        List<HAConfigTableRow> rows = new ArrayList<HAConfigTableRow>(table.getRowCount());
        for (int i = 0; i < table.getRowCount(); i++) {
            HAConfigTableRow row = new HAConfigTableRow();
            DataRow dr = table.getRow(i);
            ArrayList<String> vals = new ArrayList<String>(table.getColumnCount());
            for (int j = 0; j < table.getColumnCount(); j++) {
                if (dr.getObject(j + 1) == null)
                    vals.add("");
                else {
                    if (dr.getObject(j + 1).toString().indexOf("ClobImpl") >= 0) {
                        Clob clob = ((Clob) dr.getObject(j + 1));
                        String s = clob.getSubString(1, (int) clob.length());
                        vals.add(s);
                    } else
                        vals.add(dr.getObject(j + 1).toString());
                }
            }
            row.setValues(vals);
            rows.add(row);
        }

        tableBuffer.setRows(rows);
        return tableBuffer;
    }

    public String loadConfigText(String path) throws Exception {
        String nodePath = ZKConfigPathUtil.getConfigChildTextPath(path);

        Stat stat = new Stat();
        byte[] buffer = zkConnector.getZookeeper().getData(nodePath, new ConfigTextWatcher(zkConnector), stat);

        ConfigTextArgs configTextArgs = new ConfigTextArgs();
        configTextArgs.parsePbFrom(buffer);

        ZKConfigDataManager.putConfigText(path, ConfigType.TEXT, configTextArgs.getConfigText());

        return configTextArgs.getConfigText();
    }

    public void subscribeData(SubscribeDataArgs args) {
        try {
            ZooKeeper zk = zkConnector.getZookeeper();

            if (!zkConnector.existsNode(ConstantKey.ZK_SUBSCRIBE_ROOT_NOTE_PATH)) {
                zk.create(ConstantKey.ZK_SUBSCRIBE_ROOT_NOTE_PATH, " ".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            String nodePath = String.format("%s/%s", ConstantKey.ZK_SUBSCRIBE_ROOT_NOTE_PATH, args.getPath());

            if (!zkConnector.existsNode(nodePath)) {
                zk.create(nodePath, " ".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            nodePath = String.format("%s/%s/%s:%s", ConstantKey.ZK_SUBSCRIBE_ROOT_NOTE_PATH, args.getPath(), args.getServiceName(), args.getMachineAddress());

            if (!zkConnector.existsNode(nodePath)) {
                zkConnector.createExtraENode(nodePath, args.toPbByteArray(), false);
//                zk.create(nodePath, args.toPbByteArray(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            } else {
                Stat stat = new Stat();
                byte[] buffer = zk.getData(nodePath, false, stat);

                SubscribeDataArgs args2 = new SubscribeDataArgs();
                args2.parsePbFrom(buffer);

                args.setSubscribeTime(args2.getSubscribeTime());

                zk.delete(nodePath, -1);
//                zkConnector
                zkConnector.createExtraENode(nodePath, args.toPbByteArray(), false);
//                zk.create(nodePath, args.toPbByteArray(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            }
        } catch (Exception ex) {
            LOGGER.error(String.format("subscribeData is error, %s", ex.getMessage()), ex);
        }
    }
}
