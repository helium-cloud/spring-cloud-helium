package org.helium.hbase.common.task;

import org.helium.database.sharding.ShardedDatabase;
import org.helium.database.task.TaskQueueMysql;
import org.helium.framework.annotations.FieldSetter;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.spi.task.TaskArgs;
import org.helium.framework.tag.Initializer;
import org.helium.framework.task.TaskConsumer;
import org.helium.framework.task.TaskQueue;
import org.helium.framework.task.TaskStorageType;
import org.helium.hbase.HBaseClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

//TODO 待实现
@ServiceImplementation(id = TaskQueue.BEAN_ID + TaskStorageType.HBASE_TYPE)
public class TaskQueueHBase implements TaskQueue{

    @FieldSetter("${TASK_HB}")
    private HBaseClient hBaseClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskQueueMysql.class);

    @Initializer
    public void init() {
        try {

        } catch (Exception e) {
            LOGGER.error("TaskQueueMysql init error.{}", e);
        }
    }


    @Override
    public void put(int partition, TaskArgs taskArgs) {

    }

    @Override
    public List<TaskArgs> poolList(int partition) {
        return null;
    }

    @Override
    public TaskArgs pool(int partition) {
        return null;
    }

    @Override
    public void delete(int partition, TaskArgs taskArgs) {

    }

    @Override
    public void delete(int partition, List<TaskArgs> list) {

    }

    @Override
    public boolean isEmpty(int partition) {
        return false;
    }
}
