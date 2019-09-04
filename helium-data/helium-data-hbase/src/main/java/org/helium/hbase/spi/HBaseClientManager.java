package org.helium.hbase.spi;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.helium.framework.BeanContext;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.hbase.HBaseClient;
import org.helium.hbase.HTableClient;
import org.helium.util.StringUtils;
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
public class HBaseClientManager {

    private ConfigProvider configProvider;

    private static final Map<String, HBaseClient> hbaseClients = new ConcurrentHashMap<>();

    private static final String HBASE_CONFIG_PATH = "hbase" + File.separator;

    public static final HBaseClientManager INSTANCE = new HBaseClientManager();

    private static final Logger LOGGER = LoggerFactory.getLogger(HBaseClientManager.class);

    /**
     * 初始化对象
     */
    private HBaseClientManager() {
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
     * @param hbaseName
     * @return
     */
    public HBaseClient getHBaseClient(String hbaseName) {

        HBaseClient client = hbaseClients.get(hbaseName);
        if (client != null) {
            return client;
        }
        synchronized (hbaseClients) {
            client = hbaseClients.get(hbaseName);
            if (client != null) {
                return client;
            }
            // Load Config
            Properties prop = configProvider.loadProperties(HBASE_CONFIG_PATH + hbaseName + ".properties");
            if (prop == null) {
                throw new IllegalArgumentException("hbase config not found. path : " + HBASE_CONFIG_PATH + hbaseName + ".properties");
            }
            try {
                Connection connection = createConnection(prop);
                client = new HBaseClientImpl(connection, prop);
                hbaseClients.put(hbaseName, client);
                return client;
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
    public HBaseClient getHBaseClient(String tableName, Properties prop) {

        try {
            Connection connection = createConnection(prop);
            HBaseClient client = new HBaseClientImpl(connection, prop);
            hbaseClients.put(tableName, client);
            return client;
        } catch (Exception e) {
            LOGGER.error("Create HTable:{} failed.", tableName, e);
            throw new RuntimeException("Create HTable failed.", e);
        }

    }

}
