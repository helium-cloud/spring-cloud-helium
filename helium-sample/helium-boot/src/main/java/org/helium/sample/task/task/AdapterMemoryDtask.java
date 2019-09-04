package org.helium.sample.task.task;

import org.helium.framework.annotations.TaskImplementation;
import org.helium.framework.task.DedicatedTask;
import org.helium.framework.task.DedicatedTaskContext;
import org.helium.framework.task.TaskStorageType;
import org.helium.sample.adapter.common.MessageArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AdapterTask
		 */
@TaskImplementation(event = AdapterMemoryDtask.TASK_EVENT, storage = TaskStorageType.MEMORY_TYPE)
public class AdapterMemoryDtask implements DedicatedTask<MessageArgs> {
	public static final String TASK_EVENT = "simple:AdapterMemoryDtask";
	private static Logger LOGGER = LoggerFactory.getLogger(AdapterMemoryDtask.class);



	@Override
	public void processTask(DedicatedTaskContext ctx, MessageArgs args) {
		LOGGER.info("{}", ctx);
		ctx.setTaskRunnable();
		try {
			//30 ms 2000qps
			//10 ms 6000qps
			//5 ms 15000qps
			Thread.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {

		}
	}

	@Override
	public void processTaskRemoved(DedicatedTaskContext ctx) {
		LOGGER.info("{}", ctx.isTaskRunning());
	}
}
