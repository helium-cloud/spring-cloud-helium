package org.helium.fastdfs.spi;

import org.helium.database.ConnectionString;
import org.helium.fastdfs.FastDFSClient;
import org.helium.framework.BeanContext;
import org.helium.framework.configuration.ConfigProvider;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lvmingwei on 16-1-4.
 */
public class FastDFSManager {
    private static final String FDFS_CONFIG_PATH = "fdfs" + File.separator;

    private ConfigProvider configProvider;
    private Map<String, FastDFSClient> map;
    public static FastDFSManager INSTANCE = new FastDFSManager();

    private FastDFSManager() {
        map = new HashMap<>();
        if (BeanContext.getContextService() != null) {
            configProvider = BeanContext.getContextService().getService(ConfigProvider.class);
        }
    }

    public FastDFSClient getFastDFSClient(String fastDFSName) {

        if (configProvider != null) {
            String txt = configProvider.loadText(FDFS_CONFIG_PATH + fastDFSName + ".properties");
            return getFastDFSClient(fastDFSName, txt);
        }

        return null;
    }

    public FastDFSClient getFastDFSClient(String fastDFSName, String txt) {
        FastDFSClient client = map.get(fastDFSName);
        if (client != null) {
            return client;
        }
        client = getAndUpdateFastDFSClient(fastDFSName, txt);
        return client;
    }

    public FastDFSClient getAndUpdateFastDFSClient(String name, String txt) {
        FastDFSClient client;
        try {
            ConnectionString connectionString = ConnectionString.fromText(txt);
            client = new FastDFSClient(connectionString.getProperties());
        } catch (Exception e) {
            throw new RuntimeException("Initial fastDFS client failed.", e);
        }

        synchronized (this) {
            map.put(name, client);
        }

        return client;
    }
}
