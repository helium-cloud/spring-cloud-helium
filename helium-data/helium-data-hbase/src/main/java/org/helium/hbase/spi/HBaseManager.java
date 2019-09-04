package org.helium.hbase.spi;

import org.helium.util.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.helium.framework.BeanContext;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.hbase.HTableClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lvmingwei on 16-6-22.
 */
public class HBaseManager {

    private ConfigProvider configProvider;

    private static final Map<String, HTableClient> tables = new ConcurrentHashMap<>();

    private static final String HBASE_CONFIG_PATH = "hbase" + File.separator;

    public static final HBaseManager INSTANCE = new HBaseManager();

    private static final Logger LOGGER = LoggerFactory.getLogger(HBaseManager.class);

    /**
     * 初始化对象
     */
    private HBaseManager() {
        if (BeanContext.getContextService() != null) {
            configProvider = BeanContext.getContextService().getService(ConfigProvider.class);
        }
    }

    private synchronized Connection createConnection(Properties prop) throws IOException {
        LOGGER.info("Create HBase Connection.\r\n{}", prop);
        String zkHost = (String) prop.get("hbase.zookeeper.quorum");
        if (StringUtils.isNullOrEmpty(zkHost)) {
            throw new IllegalArgumentException("hbase.zookeeper.quorum is empty.");
        }
        Configuration configuration = HBaseConfiguration.create();
        for (Object key : prop.keySet()) {
            String keyStr = String.valueOf(key);
            configuration.set(keyStr, prop.getProperty(keyStr));
        }
        return ConnectionFactory.createConnection(configuration);
    }

    /**
     * 获取一个Database
     *
     * @param tableName
     * @return
     */
    public HTableClient getHTableClient(String tableName) {

        HTableClient client = tables.get(tableName);
        if (client != null) {
            return client;
        }
        synchronized (tables) {
            client = tables.get(tableName);
            if (client != null) {
                return client;
            }
            // Load Config
            Properties prop = configProvider.loadProperties(HBASE_CONFIG_PATH + tableName + ".properties");
            if (prop == null) {
                throw new IllegalArgumentException("hbase config not found. path : " + HBASE_CONFIG_PATH + tableName + ".properties");
            }
            try {
                Connection connection = createConnection(prop);
                HTableClient table = new HTableClientImpl(connection, tableName, prop);
                tables.put(tableName, table);
                return table;
            } catch (Exception e) {
                throw new RuntimeException("Create HTable failed.", e);
            }
        }
    }


    /**
     * 获取getHTableClient
     * @param tableName
     * @param prop
     * @return
     */
    public HTableClient getHTableClient(String tableName, Properties prop) {

        try {
            Connection connection = createConnection(prop);
            HTableClient table = new HTableClientImpl(connection, tableName, prop);
            tables.put(tableName, table);
            return table;
        } catch (Exception e) {
            LOGGER.error("Create HTable:{} failed.", tableName, e);
            throw new RuntimeException("Create HTable failed.", e);
        }

    }

}
