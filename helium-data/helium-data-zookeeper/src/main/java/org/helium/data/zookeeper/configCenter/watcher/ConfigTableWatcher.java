package org.helium.data.zookeeper.configCenter.watcher;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.helium.data.zookeeper.configCenter.manager.ZKConfigurationManager;
import org.helium.framework.route.zk.ZooKeeperConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zconfig.configuration.args.ConfigType;
import zconfig.configuration.args.ConfigUpdater;

import java.util.List;

/**
 * Created by liufeng on 2017/8/11.
 */
public class ConfigTableWatcher implements Watcher {
    private static Logger LOGGER = LoggerFactory.getLogger(ConfigTableWatcher.class);

    private ZooKeeperConnector zkConnector = null;

    public ConfigTableWatcher(ZooKeeperConnector zkConnector)
    {
        this.zkConnector = zkConnector;
    }

    @Override
    public void process(WatchedEvent event) {
        if(event.getType() != Event.EventType.None) {
            try {
                String[] strPath = event.getPath().split("/");
                String path = strPath[strPath.length - 1];

                List<ConfigUpdater<?>> list = ZKConfigurationManager.getInstance().getUpdaterIndex().find(path, ConfigType.TABLE, "");

                if (list != null && list.size() > 0) {
                    for (ConfigUpdater updater : list) {
                        updater.update();
                    }
                }
            } catch (Exception ex) {
                LOGGER.error(String.format("ConfigTableWatcher.process(%s, %s) FAILED!", event.getPath(), "table"), ex);
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
