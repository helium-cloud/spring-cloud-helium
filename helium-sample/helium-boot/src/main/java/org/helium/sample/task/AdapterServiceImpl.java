package org.helium.sample.task;

import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.annotations.TaskEvent;
import org.helium.framework.task.TaskProducer;
import org.helium.sample.adapter.common.MessageArgs;
import org.helium.sample.task.task.AdapterMemoryDtask;
import org.helium.sample.task.task.AdapterMemoryTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Coral on 9/10/16.
 */
@ServiceImplementation(id = "simple:AdapterService")
public class AdapterServiceImpl implements AdapterService {

	@TaskEvent(AdapterMemoryTask.TASK_EVENT)
	private TaskProducer<MessageArgs> taskProducer;

	@TaskEvent(AdapterMemoryDtask.TASK_EVENT)
	private TaskProducer<MessageArgs> taskProducerDt;
	private static Logger LOGGER = LoggerFactory.getLogger(AdapterMemoryTask.class);

	@Override
	public void adapter(MessageArgs messageArgs) {
		LOGGER.info("AdapterService:{}", messageArgs.toJsonObject().toString());
//		taskProducer.produce(messageArgs);
		taskProducerDt.produce(messageArgs);

	}
}
