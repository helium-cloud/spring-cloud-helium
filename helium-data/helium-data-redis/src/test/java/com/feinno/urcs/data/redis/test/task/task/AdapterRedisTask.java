package com.feinno.urcs.data.redis.test.task.task;

import com.feinno.urcs.data.redis.test.task.AdapterTaskArgs;
import org.helium.framework.annotations.TaskImplementation;
import org.helium.framework.task.Task;
import org.helium.framework.task.TaskStorageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AdapterTask
		 */
@TaskImplementation(event = AdapterRedisTask.TASK_EVENT, storage = TaskStorageType.REDIS_TYPE)
public class AdapterRedisTask implements Task<AdapterTaskArgs> {
	public static final String TASK_EVENT = "simple:AdapterRedisTask";
	private static Logger LOGGER = LoggerFactory.getLogger(AdapterRedisTask.class);

	@Override
	public void processTask(AdapterTaskArgs args) {
		LOGGER.info("AdapterRedisTask:processTask{}", args.toPbByteArray());
	}

}
