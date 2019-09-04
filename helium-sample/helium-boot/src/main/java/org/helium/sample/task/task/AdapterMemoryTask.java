package org.helium.sample.task.task;

import org.helium.framework.annotations.TaskImplementation;
import org.helium.framework.task.Task;
import org.helium.framework.task.TaskStorageType;
import org.helium.sample.adapter.common.MessageArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AdapterTask
		 */
@TaskImplementation(event = AdapterMemoryTask.TASK_EVENT, storage = TaskStorageType.MEMORY_TYPE)
public class AdapterMemoryTask implements Task<MessageArgs> {
	public static final String TASK_EVENT = "simple:AdapterMemoryTask";
	private static Logger LOGGER = LoggerFactory.getLogger(AdapterMemoryTask.class);

	@Override
	public void processTask(MessageArgs args) {
		LOGGER.info("{}", args.toJsonObject().toString());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}


}
