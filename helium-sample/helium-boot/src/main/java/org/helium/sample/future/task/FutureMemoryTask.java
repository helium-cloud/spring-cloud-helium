package org.helium.sample.future.task;

import org.helium.framework.annotations.TaskImplementation;
import org.helium.framework.task.BatchTask;
import org.helium.sample.future.common.MessageRequest;
import org.helium.sample.future.common.QueueName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * FutureMemoryTask
 */
@TaskImplementation(event = FutureMemoryTask.TASK_EVENT, storage = QueueName.FutureMemoryTask)
public class FutureMemoryTask implements BatchTask<MessageRequest> {
	public static final String TASK_EVENT = "simple:FutureMemoryTask";
	private static Logger LOGGER = LoggerFactory.getLogger(FutureMemoryTask.class);


	@Override
	public void processTask(List<MessageRequest> argList) {
		try {
			Thread.sleep(10);
			LOGGER.error("process:{}", argList.size());
//			for (MessageRequest messageRequest :argList) {
//				LOGGER.info("process:{}", "iii");
//				 messageRequest.getMessageResponseFuture().complete(new MessageResponse());
//			}
		} catch (InterruptedException e) {
			LOGGER.error("processTask:{}", e);
		}
	}
}
