package org.helium.data.zookeeper.configCenter.watcher;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.helium.data.zookeeper.utils.CommonUtils;
import org.helium.framework.route.zk.ZooKeeperConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liufeng on 2017/8/11.
 */
public class DBConnectionStringWatcher implements Watcher {
    private static Logger LOGGER = LoggerFactory.getLogger(DBConnectionStringWatcher.class);

    private ZooKeeperConnector zkConnector = null;

    public DBConnectionStringWatcher(ZooKeeperConnector zkConnector) {
        this.zkConnector = zkConnector;
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getType() != Event.EventType.None) {
            try {
                String[] strPath = event.getPath().split("/");
                String path = strPath[strPath.length - 1];

                int index = path.indexOf(".");
                String databaseName = path.substring(0, index);

                CommonUtils.getDatabase(event.getPath(), databaseName, zkConnector);
            } catch (Exception ex) {
                LOGGER.error(String.format("DBConnectionStringWatcher.process(%s, %s) FAILED!", event.getPath(), "text"), ex);
            } finally {
                try {
                    Stat stat = new Stat();
                    zkConnector.getZookeeper().getData(event.getPath(), this, stat);
                } catch (Exception ex) {
                }
            }
        }
    }
}
