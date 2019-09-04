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
@TaskImplementation(event = AdapterMysqlDtask.TASK_EVENT, storage = TaskStorageType.MYSQL_TYPE)
public class AdapterMysqlDtask implements DedicatedTask<AdapterTaskArgs> {
	public static final String TASK_EVENT = "simple:AdapterMysqlDtask";
	private static Logger LOGGER = LoggerFactory.getLogger(AdapterMysqlDtask.class);



	@Override
	public void processTask(DedicatedTaskContext ctx, AdapterTaskArgs args) {
		LOGGER.info("{}", args.toJsonObject().toString());
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void processTaskRemoved(DedicatedTaskContext ctx) {
		LOGGER.info("{}", ctx.isTaskRunning());
	}
}
