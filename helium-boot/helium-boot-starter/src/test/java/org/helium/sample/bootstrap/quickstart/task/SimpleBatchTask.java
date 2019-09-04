package org.helium.sample.bootstrap.quickstart.task;

import org.helium.framework.annotations.TaskImplementation;
import org.helium.framework.task.BatchTask;

import org.helium.sample.bootstrap.quickstart.common.MessageRequest;
import org.helium.sample.bootstrap.quickstart.common.QueueName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * SimpleBatchTask
 */
@TaskImplementation(event = SimpleBatchTask.TASK_EVENT, storage = QueueName.FutureMemoryTask)
public class SimpleBatchTask implements BatchTask<MessageRequest> {
	public static final String TASK_EVENT = "simple:SimpleBatchTask";
	private static Logger LOGGER = LoggerFactory.getLogger(SimpleBatchTask.class);
	@Override
	public void processTask(List<MessageRequest> argList) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT INTO `test_tb` (`name`, `desc`) VALUES");
			for (MessageRequest messageRequest : argList) {
				sb.append("('" + messageRequest.getMobile() + "', '" + messageRequest.getMobile() + "'),");
			}
			sb.deleteCharAt(sb.toString().length() -1 );
			LOGGER.info("process:{}", sb.toString());
			LOGGER.info("process:{}", argList.size());
		} catch (Exception e) {
			LOGGER.error("processTask:{}", e);
		}
	}
}
