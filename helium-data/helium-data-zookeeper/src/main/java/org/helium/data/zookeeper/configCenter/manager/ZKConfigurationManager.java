package org.helium.data.zookeeper.configCenter.manager;

import com.feinno.superpojo.util.StringUtils;
import org.apache.zookeeper.data.Stat;
import org.helium.data.zookeeper.configCenter.entity.ZKConfigTable;
import org.helium.data.zookeeper.configCenter.task.ConfigTaskEvent;
import org.helium.data.zookeeper.configCenter.watcher.ConfigTextWatcher;
import org.helium.database.DataTable;
import org.helium.database.Database;
import org.helium.database.spi.DatabaseManager;
import org.helium.framework.BeanContext;
import org.helium.framework.configuration.Environments;
import org.helium.framework.route.zk.ZooKeeperConnector;
import org.helium.framework.spi.Bootstrap;
import org.helium.framework.task.TaskProducer;
import org.helium.framework.task.TaskProducerFactory;
import org.helium.util.ServiceEnviornment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zconfig.args.ConfigUpdateNotifyArgs;
import zconfig.args.SubscribeDataArgs;
import zconfig.configuration.args.*;
import zconfig.enums.LoadDataMode;
import zconfig.utils.ConfigUtils;
import zconfig.utils.ZKConfigPathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * zk数据同步
 * 1.从URCS_ZKDB加载进入zk
 */
public class ZKConfigurationManager {
    private static Logger LOGGER = LoggerFactory.getLogger(ZKConfigurationManager.class);

    private static ZKConfigurator configurator;

    private static ZKConfigurationManager instance;

    private static Object syncObject = new Object();
    private static Object syncRoot = new Object();

    private static List<ConfigUpdater<?>> updaters;
    private static SearchIndex<ConfigUpdater<?>> updaterIndex;

    private TaskProducer<ConfigUpdateNotifyArgs> configUpdateNotifyTask;

    private static Map<String, Database> databases;

    private ZKConfigurationManager() {
        updaters = new ArrayList<>();
        String[] params = new String[]{"path", "type", "params"};

        try {
            updaterIndex = new SearchIndex(ConfigUpdater.class, updaters, params);

            TaskProducerFactory factory = BeanContext.getContextService().getService(TaskProducerFactory.class);


            configUpdateNotifyTask = (TaskProducer<ConfigUpdateNotifyArgs>) factory.getProducer(ConfigTaskEvent.CONFIG_UPDATE_NOTIFY);

            databases = new ConcurrentHashMap<>();
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
        }
    }

    private static void initialize() {
        if (instance == null) {
            synchronized (syncObject) {
                if (instance == null) {
                    instance = new ZKConfigurationManager();
                }
            }
        }
    }

    public static ZKConfigurationManager getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    public void setConfigurator(ZKConfigurator configurator) {
        this.configurator = configurator;
    }

    public SearchIndex<ConfigUpdater<?>> getUpdaterIndex() {
        return updaterIndex;
    }

    public <K, V extends ConfigTableItem> ZKConfigTable<K, V> loadTable(final Class<K> keyType,
                                                                        final Class<V> valueType,
                                                                        final String path) throws ConfigurationException {
        try {
            LOGGER.info("Start load ConfigTable {}={} path={}", keyType, valueType, path);
            ZKConfigTable<K, V> table = configurator.loadConfigTable(keyType, valueType, path);
            LOGGER.info("End load ConfigTable {}={} path={}", keyType, valueType, path);
            insertConfigUpdateNotify(path, ConfigType.TABLE, LoadDataMode.INIT);

            return table;
        } catch (Exception e) {
            LOGGER.error(String.format("ZKConfigurationManager.loadTable failed! tableName: %s, error: %s", path, e.getMessage()), e);

            throw new ConfigurationException("loadTable failed: " + path, e);
        }
    }

    public <K, V extends ConfigTableItem> void callBackConfigData(final Class<K> keyType,
                                                                  final Class<V> valueType,
                                                                  final String path,
                                                                  final ConfigUpdateAction<ZKConfigTable<K, V>> updateCallback) throws ConfigurationException {
        try {

            //同时放一份到helium,由helium维护节点断线重连等
            ZooKeeperConnector zooKeeperConnector = configurator.getZkConnector();
            Stat stat = new Stat();
            zooKeeperConnector.storeExtraMonitor(ZKConfigPathUtil.getConfigChildTablePath(path), new ConfigTextWatcher(zooKeeperConnector), stat);


            ConfigUpdater<ConfigTable<K, V>> updater = new ConfigUpdater<ConfigTable<K, V>>(path, ConfigType.TABLE, "") {
                @Override
                public void update() throws Exception {
                    LOGGER.info("update ConfigTable {}={} path={}", keyType, valueType, path);
                    ZKConfigTable<K, V> table = configurator.loadConfigTable(keyType, valueType, path);

                    if (updateCallback != null) {
                        insertConfigUpdateNotify(path, ConfigType.TABLE, LoadDataMode.UPDATE);

                        updateCallback.run(table);
                    }

                    ZKConfigDataManager.putConfigTable(path, ConfigType.TABLE, table);
                }
            };

            synchronized (syncRoot) {
                updaters.add(updater);
                updaterIndex.build(updaters);
            }
        } catch (Exception ex) {
            throw new ConfigurationException(ex.getMessage());
        }
    }

    public String loadText(final String path) throws ConfigurationException {
        try {
            LOGGER.info("Start load ConfigText path={}", path);
            String strResult = configurator.loadConfigText(path);
            LOGGER.info("End load ConfigText path={}", path);
            insertConfigUpdateNotify(path, ConfigType.TEXT, LoadDataMode.INIT);

            return strResult;
        } catch (Exception e) {
            LOGGER.error(String.format("ZKConfigurationManager.loadText failed! path: %s, error: %s", path, e.getMessage()), e);

            throw new ConfigurationException("loadText error", e);
        }
    }

    public void callBackConfigData(final String path, final ConfigUpdateAction<String> updateCallback) throws ConfigurationException {
        try {
            //同时放一份到helium,由helium维护节点断线重连等
            ZooKeeperConnector zooKeeperConnector = configurator.getZkConnector();
            Stat stat = new Stat();
            zooKeeperConnector.storeExtraMonitor(ZKConfigPathUtil.getConfigChildTextPath(path), new ConfigTextWatcher(zooKeeperConnector), stat);

            // 订阅用于配置变动时的更新
            ConfigUpdater<String> updater = new ConfigUpdater<String>(path, ConfigType.TEXT, "") {
                @Override
                public void update() throws Exception {
                    LOGGER.info("update ConfigText path={}", path);
                    String text = configurator.loadConfigText(path);

                    if (updateCallback != null) {
                        insertConfigUpdateNotify(path, ConfigType.TEXT, LoadDataMode.UPDATE);

                        updateCallback.run(text);
                    }
                }
            };
            synchronized (syncRoot) {
                updaters.add(updater);
                updaterIndex.build(updaters);
            }
        } catch (Exception ex) {
            throw new ConfigurationException(ex.getMessage());
        }
    }

    private void insertConfigUpdateNotify(String path, ConfigType configType, LoadDataMode loadDataMode) {
        try {
            ConfigUpdateNotifyArgs args = new ConfigUpdateNotifyArgs();
            args.setConfigKey(path);
            args.setConfigType(configType);
            args.setLoadDataMode(loadDataMode);

            String serviceName = Bootstrap.INSTANCE.getServiceName();
            if (!StringUtils.isNullOrEmpty(serviceName)) {
                args.setServiceName(serviceName);
            }

            if (Environments.getVar("PRIVATE_IP") != null) {
                args.setMachineAddress(Environments.getVar("PRIVATE_IP"));
            } else {
                args.setMachineAddress(ServiceEnviornment.getComputerName());
            }

            args.setLastReadTime(ConfigUtils.getCurrentTime());

            if (loadDataMode == LoadDataMode.UPDATE) {
                String latestLoadDataTime = getDBVersion(path, configType);

                configurator.subscribeData(new SubscribeDataArgs(path, args.getServiceName(), args.getMachineAddress(), args.getLastReadTime(), latestLoadDataTime));

                configUpdateNotifyTask.produce(args);
            } else if (loadDataMode == LoadDataMode.INIT) {
                String latestLoadDataTime = getDBVersion(path, configType);

                configurator.subscribeData(new SubscribeDataArgs(path, args.getServiceName(), args.getMachineAddress(), args.getLastReadTime(), latestLoadDataTime));

                Database database = DatabaseManager.INSTANCE.getDatabase(Environments.getVar("URCS_ZKDB"));

                String insertSql = "INSERT INTO URCS_ConfigUpdateNotify(`ConfigKey`,`ConfigType`,`LoadDataMode`, `ServiceName`,`MachineAddress`,`LastReadTime`) VALUES(?, ?, ?, ?, ?, ?)";

                database.executeInsert(insertSql,
                        args.getConfigKey(),
                        args.getConfigType().toString().toLowerCase(),
                        args.getLoadDataMode().getName(),
                        args.getServiceName(),
                        args.getMachineAddress(),
                        args.getLastReadTime());
            }
        } catch (Exception ex) {
            LOGGER.error(String.format("insertConfigUpdateNotify is error, %s", ex.getMessage()), ex);
        }
    }

    public void putDatabase(String databaseName, Database db) {
        databases.put(databaseName, db);
    }

    public Database getDatabase(String databaseName) {
        if (databases.containsKey(databaseName)) {
            return databases.get(databaseName);
        } else {
            return null;
        }
    }

    public String getDBVersion(String path, ConfigType configType) {
        String version = null;

        try {
            Database database = DatabaseManager.INSTANCE.getDatabase(Environments.getVar("URCS_ZKDB"));

            if (configType == ConfigType.TABLE) {
                String sql = String.format("select * from URCS_ConfigTable where TableName = '%s'", path);

                DataTable dataTable = database.executeTable(sql);

                version = ConfigUtils.convertDateToString(dataTable.getRow(0).getDateTime("Version"));
            } else if (configType == ConfigType.TEXT) {
                String sql = String.format("select * from URCS_ConfigText where ConfigKey = '%s'", path);

                DataTable dataTable = database.executeTable(sql);

                version = ConfigUtils.convertDateToString(dataTable.getRow(0).getDateTime("Version"));
            }
        } catch (Exception ex) {
            LOGGER.error(String.format("getDBVersion is error, %s", ex.getMessage()), ex);
        }

        return version;
    }
}
