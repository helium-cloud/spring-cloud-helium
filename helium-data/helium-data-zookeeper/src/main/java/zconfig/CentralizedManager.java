package zconfig;

import org.apache.zookeeper.ZooKeeper;
import org.helium.framework.route.zk.ZooKeeperConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liufeng on 2017/8/10.
 */
public class CentralizedManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CentralizedManager.class);

    private ZooKeeperConnector zkConnector;

    private Object syncObject = new Object();

    private String zkHosts;

    private CentralizedManager() {
    }

    private volatile static CentralizedManager centralizedManager;

    public static CentralizedManager getInstance() {
        if (centralizedManager == null) {
            synchronized (CentralizedManager.class) {
                if (centralizedManager == null) {
                    centralizedManager = new CentralizedManager();
                }
            }
        }
        return centralizedManager;
    }

    public void connect(String zkHosts) throws Exception {
        if (zkConnector == null) {
            synchronized (syncObject) {
                if (zkConnector == null) {
                    LOGGER.info("try connect ZooKeeper: {}", zkHosts);
                    zkConnector = new ZooKeeperConnector(zkHosts);
                    zkConnector.connect();
                    LOGGER.info("try connect ZooKeeper: {}", zkHosts);

                    this.zkHosts = zkHosts;
                }
            }
        }
    }

    public String getzkHosts() {
        return zkHosts;
    }

    public ZooKeeper getZookeeper() {
        return zkConnector.getZookeeper();
    }

    public ZooKeeperConnector getZooKeeperConnector() {
        return zkConnector;
    }
}
