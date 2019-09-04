package org.helium.redis.data.task;

import com.feinno.superpojo.util.StringUtils;
import org.helium.framework.annotations.FieldSetter;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.annotations.ServiceSetter;
import org.helium.framework.spi.task.TaskArgs;
import org.helium.framework.tag.Initializer;
import org.helium.framework.task.TaskConsumer;
import org.helium.framework.task.TaskQueue;
import org.helium.framework.task.TaskStorageType;
import org.helium.framework.utils.Closeable;
import org.helium.redis.RedisClient;
import org.helium.redis.cluster.RedisSentinelCluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.ArrayList;
import java.util.List;

@ServiceImplementation(id = TaskQueue.BEAN_ID + TaskStorageType.REDIS_TYPE)
public class TaskQueueRedis implements TaskQueue {

    public static final Logger LOGGER = LoggerFactory.getLogger(TaskQueueRedis.class);
    public static final String TASK_RD = "TASK_RD:";
    public String serverId = null;

    @FieldSetter("${TASK_RD}")
    private RedisSentinelCluster sentinelCluster;

    @ServiceSetter
    private TaskConsumer taskConsumer;

    @Initializer
    public void init() {
        try {
            LOGGER.info("TaskQueueRedis init start");
            taskConsumer.putStorage(TaskStorageType.REDIS_TYPE, this);
            //taskConsumer
            LOGGER.info("TaskQueueRedis init complete");
        } catch (Exception e) {
            LOGGER.error("TaskQueueRedis init error.{}", e);
        }
    }

    public long getLimit() {
        return 1000;
    }

    private String getQueueName(int partition) {
        return getTaskRd() + partition;
    }

    @Override
    public void put(int partition, TaskArgs taskArgs) {
        try {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("put " + taskArgs.toJsonObject().toString());
            }
            RedisClient redisClient = sentinelCluster.getClient();
            redisClient.rpush(getQueueName(partition), taskArgs);
        } catch (Exception e) {
            LOGGER.error("put error:", e);
        }
    }

    @Override
    public List<TaskArgs> poolList(int partition) {
        List<TaskArgs> list = new ArrayList<TaskArgs>();
        try {

            RedisClient redisClient = sentinelCluster.getClient();
            long limitSize = getLimit();
            long length = redisClient.llen(getQueueName(partition));
            if (length == 0) {
                return list;
            }
            if (length <= getLimit()) {
                limitSize = length;
            }
            list = redisClient.lrange(getQueueName(partition), 0, limitSize, TaskArgs.class);
            return list;
        } catch (Exception e) {
            LOGGER.error("poolList error!e={}", e);
        }
        return list;
    }

    @Override
    public TaskArgs pool(int partition) {
        TaskArgs taskArgs = null;
        RedisClient redisClient = sentinelCluster.getClient();

        long size = redisClient.llen(getQueueName(partition));
        if (size == 0) {
            return null;
        }
        taskArgs = redisClient.lpop(getQueueName(partition), TaskArgs.class);

        return taskArgs;
    }

    @Override
    public void delete(int partition, TaskArgs taskArgs) {

    }

    @Override
    public void delete(int partition, List<TaskArgs> list) {
        Closeable<Pipeline> pipelineCloseable = sentinelCluster.getClient().getPipeline(getQueueName(partition));
        try {
            Pipeline pipeline = pipelineCloseable.get();
            for (int i = list.size(); i > 0; i--) {
               Response<String> response = pipeline.lpop(getQueueName(partition));
            }
            pipeline.sync();
        } catch (Exception e){
            LOGGER.error("delete", e);
        } finally {
            pipelineCloseable.close();
        }

    }

    @Override
    public boolean isEmpty(int partition) {
        return false;
    }

    public String getTaskRd() {
        if (StringUtils.isNullOrEmpty(serverId)){
            synchronized (this){
                if (StringUtils.isNullOrEmpty(serverId)){
                    //TODO 需要定义服务名称
                    serverId = "local:";
                }
            }
        }
        return serverId + "-" + TASK_RD;
    }
}
