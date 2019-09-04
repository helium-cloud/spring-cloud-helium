package com.feinno.urcs.data.redis.test.task.task;

import com.feinno.urcs.data.redis.test.task.AdapterTaskArgs;
import org.helium.framework.annotations.TaskImplementation;
import org.helium.framework.task.DedicatedTask;
import org.helium.framework.task.DedicatedTaskContext;
import org.helium.framework.task.TaskStorageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AdapterTask
		 */
@TaskImplementation(event = AdapterMemoryDtask.TASK_EVENT, storage = TaskStorageType.MEMORY_TYPE)
public class AdapterMemoryDtask implements DedicatedTask<AdapterTaskArgs> {
	public static final String TASK_EVENT = "simple:AdapterMemoryDtask";
	private static Logger LOGGER = LoggerFactory.getLogger(AdapterMemoryDtask.class);



	@Override
	public void processTask(DedicatedTaskContext ctx, AdapterTaskArgs args) {
		LOGGER.info("{}", args.toJsonObject().toString());
		try {
			//Thread.sleep(1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ctx.setTaskRunnable();
		}
	}

	@Override
	public void processTaskRemoved(DedicatedTaskContext ctx) {
		LOGGER.info("{}", ctx.isTaskRunning());
	}
}
