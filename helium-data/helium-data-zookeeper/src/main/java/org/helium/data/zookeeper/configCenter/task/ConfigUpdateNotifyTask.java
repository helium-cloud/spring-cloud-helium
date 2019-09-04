package org.helium.data.zookeeper.configCenter.task;

import org.helium.framework.annotations.ServiceSetter;
import org.helium.framework.annotations.TaskImplementation;
import org.helium.framework.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zconfig.api.ConfigDataDao;
import zconfig.args.ConfigUpdateNotifyArgs;

/**
 * Created by liufeng on 2017/8/14.
 */
@TaskImplementation(event = ConfigTaskEvent.CONFIG_UPDATE_NOTIFY, id="config:update-notify-task")
public class ConfigUpdateNotifyTask implements Task<ConfigUpdateNotifyArgs> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUpdateNotifyTask.class);

    @ServiceSetter
    private ConfigDataDao configDataDao;

    public ConfigUpdateNotifyTask()
    {
        int a = 10;
    }

    @Override
    public void processTask(ConfigUpdateNotifyArgs args) {
        LOGGER.info("ConfigUpdateNotifyTask consume taskArgs:{}", args.dumpObject());

        configDataDao.insertConfigUpdateNotify(args);
    }

    /*private void insertConfigUpdateNotify(ConfigUpdateNotifyArgs configUpdateNotifyArgs)
    {
        try {
            String insertSql = "INSERT INTO URCS_ConfigUpdateNotify(`ConfigKey`,`ConfigType`,`LoadDataMode`, `ServiceName`,`MachineAddress`,`LastReadTime`) VALUES(?, ?, ?, ?, ?, ?)";

            database.executeInsert(insertSql,
                    configUpdateNotifyArgs.getConfigKey(),
                    configUpdateNotifyArgs.getConfigType().toString().toLowerCase(),
                    configUpdateNotifyArgs.getLoadDataMode().getName(),
                    configUpdateNotifyArgs.getServiceName(),
                    configUpdateNotifyArgs.getMachineAddress(),
                    configUpdateNotifyArgs.getLastReadTime());
        } catch (Exception ex) {
            LOGGER.error(String.format("insertConfigUpdateNotify error, %s", ex.getMessage()), ex);
        }
    }*/
}
