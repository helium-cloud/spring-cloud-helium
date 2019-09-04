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
@TaskImplementation(event = AdapterMemoryTask.TASK_EVENT, storage = TaskStorageType.MEMORY_TYPE)
public class AdapterMemoryTask implements Task<AdapterTaskArgs> {
	public static final String TASK_EVENT = "simple:AdapterMemoryTask";
	private static Logger LOGGER = LoggerFactory.getLogger(AdapterMemoryTask.class);

	@Override
	public void processTask(AdapterTaskArgs args) {
		LOGGER.info("AdapterMemoryTask:processTask{}", args.toJsonObject().toString());

	}


}
