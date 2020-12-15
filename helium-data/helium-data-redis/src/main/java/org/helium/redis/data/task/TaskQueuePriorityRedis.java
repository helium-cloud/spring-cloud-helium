package org.helium.redis.data.task;

import com.feinno.superpojo.util.StringUtils;
import org.helium.cloud.task.manager.AbstractTaskConsumer;
import org.helium.framework.task.TaskArgs;
import org.helium.framework.task.TaskQueuePriority;
import org.helium.framework.task.TaskStorageType;
import org.helium.framework.utils.Closeable;
import org.helium.redis.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.util.SafeEncoder;

import java.util.ArrayList;
import java.util.List;

public class TaskQueuePriorityRedis implements TaskQueuePriority {
	public static final Logger LOGGER = LoggerFactory.getLogger(TaskQueuePriorityRedis.class);
	public static final String TASK_RD = "DT_TASK_RD:";
	public String serverId = null;
	private RedisClient redisClient;
	private AbstractTaskConsumer taskConsumer;

	public TaskQueuePriorityRedis(RedisClient redisClient, AbstractTaskConsumer taskConsumer, String serverId) {
		this.redisClient = redisClient;
		this.taskConsumer = taskConsumer;
		this.serverId = serverId;
		init();
	}

	public void init() {
		try {
			LOGGER.info("TaskQueuePriorityRedis init start");
			taskConsumer.putStorageInner(TaskStorageType.REDIS_TYPE, this);
			LOGGER.info("TaskQueuePriorityRedis init complete");
		} catch (Exception e) {
			LOGGER.error("TaskQueuePriorityRedis init error.{}", e);
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
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("put simple" + taskArgs.toJsonObject().toString());
			}
			redisClient.rpush(getQueueName(partition), taskArgs);
		} catch (Exception e) {
			LOGGER.error("put simple error:", e);
		}
	}

	@Override
	public List<TaskArgs> poolList(int partition) {
		List<TaskArgs> list = new ArrayList<TaskArgs>();
		try {
			long limitSize = getLimit();
			long length = redisClient.llen(getQueueName(partition));
			if (length == 0) {
				return list;
			}
			if (length <= getLimit()) {
				limitSize = length;
			}
			list = redisClient.lrange(getQueueName(partition), 0, limitSize, TaskArgs.class);
			if (list != null) {
				//初始值为0
				for (TaskArgs taskArgs : list) {
					taskArgs.setTid(0);
				}
			}
			return list;
		} catch (Exception e) {
			LOGGER.error("DtQueue poolList error!e={}", e);
		}
		return list;
	}

	@Override
	public void putPriority(int partition, TaskArgs taskArgs) {
		try {
			//-1表示未被消费
			taskArgs.setTid(-1);
		} catch (Exception e) {
			LOGGER.error("put Priority error:", e);
		}
	}

	@Override
	public TaskArgs pool(int partition) {
		long size = redisClient.llen(getQueueName(partition));
		if (size == 0) {
			return null;
		}
		TaskArgs taskArgs = redisClient.lpop(getQueueName(partition), TaskArgs.class);

		return taskArgs;
	}

	@Override
	public void delete(int partition, TaskArgs taskArgs) {

	}

	/**
	 * 队列 1 2 3 4 5 6 7 8 9
	 * 3，4未被消费先把3,4队尾然后pop剩余次数
	 *
	 * @param partition
	 * @param list
	 */
	@Override
	public void delete(int partition, List<TaskArgs> list) {
		Closeable<Pipeline> pipelineCloseable = redisClient.getPipeline(getQueueName(partition));
		try {
			Pipeline pipeline = pipelineCloseable.get();
			List<TaskArgs> resultPut = new ArrayList<>();
			List<TaskArgs> resultDelete = new ArrayList<>();

			for (TaskArgs taskArgs : list) {
				if (taskArgs.getTid() == -1) {
					resultPut.add(taskArgs);
				} else {
					resultDelete.add(taskArgs);
				}
			}
			int total = list.size();
			int putSize = resultPut.size();
			int putIndex = 0;
			//未消费完成同步，已消费删除
			for (int i = total - putSize; i < total; i++, putIndex++) {
				byte[] bsKey = SafeEncoder.encode(getQueueName(partition));
				byte[] bs = resultPut.get(putIndex).toPbByteArray();
				Response<String> response = pipeline.lset(bsKey, i, bs);
			}
			for (int i = resultDelete.size(); i > 0; i--) {
				Response<String> response = pipeline.lpop(getQueueName(partition));
			}
			pipeline.sync();
		} catch (Exception e) {
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
		if (StringUtils.isNullOrEmpty(serverId)) {
			synchronized (this) {
				if (StringUtils.isNullOrEmpty(serverId)) {
					//TODO 设置service-id
					serverId = "local";
				}
			}
		}
		return serverId + "-" + TASK_RD;
	}

}
