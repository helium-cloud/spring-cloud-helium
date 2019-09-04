package org.helium.sample.bootstrap.quickstart.service;

import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.annotations.TaskEvent;
import org.helium.framework.spi.Bootstrap;
import org.helium.framework.spi.task.TaskQueueMemory;
import org.helium.framework.tag.Initializer;
import org.helium.framework.task.TaskConsumer;
import org.helium.framework.task.TaskProducer;
import org.helium.sample.bootstrap.quickstart.common.MessageRequest;
import org.helium.sample.bootstrap.quickstart.common.MessageResponse;
import org.helium.sample.bootstrap.quickstart.task.SimpleBatchTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.helium.sample.bootstrap.quickstart.common.QueueName.FutureMemoryTask;

/**
 * Created by Leon on 9/10/16.
 */
@ServiceImplementation
public class SimpleServiceImpl implements SimpleService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleServiceImpl.class);

	@TaskEvent(SimpleBatchTask.TASK_EVENT)
	private TaskProducer<MessageRequest> messageRequestTaskProducer;

	@Initializer
	public void init(){
		try {
			LOGGER.info("init start:{}", FutureMemoryTask);
			TaskConsumer taskConsumer = (TaskConsumer) Bootstrap.INSTANCE.getBean("helium:TaskConsumer").getBean();
			taskConsumer.putBatchStorage(FutureMemoryTask,  new TaskQueueMemory());
			LOGGER.info("init complete:{}", FutureMemoryTask);
		} catch (Exception e) {
			LOGGER.error("init error.{}", e);
		}
	}

	@Override
	public MessageResponse send(MessageRequest messageRequest) {
		messageRequestTaskProducer.produce(messageRequest);
		return new MessageResponse();
	}
}
