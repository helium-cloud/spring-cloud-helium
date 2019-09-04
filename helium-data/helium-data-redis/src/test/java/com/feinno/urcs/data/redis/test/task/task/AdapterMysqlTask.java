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
@TaskImplementation(event = AdapterMysqlTask.TASK_EVENT, storage = TaskStorageType.MYSQL_TYPE)
public class AdapterMysqlTask implements Task<AdapterTaskArgs> {
	public static final String TASK_EVENT = "simple:AdapterMysqlTask";
	private static Logger LOGGER = LoggerFactory.getLogger(AdapterMysqlTask.class);

	@Override
	public void processTask(AdapterTaskArgs args) {
		LOGGER.info("AdapterMysqlTask:processTask{}", args.toJsonObject().toString());

	}


}
