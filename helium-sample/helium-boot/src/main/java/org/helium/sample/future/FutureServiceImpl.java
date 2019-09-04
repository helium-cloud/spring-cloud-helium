package org.helium.sample.future;

import org.helium.threading.Future;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.annotations.TaskEvent;
import org.helium.framework.spi.Bootstrap;
import org.helium.framework.spi.task.TaskQueueMemory;
import org.helium.framework.tag.Initializer;
import org.helium.framework.task.TaskConsumer;
import org.helium.framework.task.TaskProducer;
import org.helium.sample.future.common.MessageRequest;
import org.helium.sample.future.common.MessageResponse;
import org.helium.sample.future.common.QueueName;
import org.helium.sample.future.task.FutureMemoryTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Coral on 9/10/16.
 */
@ServiceImplementation
public class FutureServiceImpl implements FutureService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FutureServiceImpl.class);

	@TaskEvent(FutureMemoryTask.TASK_EVENT)
	private TaskProducer<MessageRequest> messageRequestTaskProducer;

	@Initializer
	public void init(){
		try {
			LOGGER.info("init start:{}", QueueName.FutureMemoryTask);
			TaskConsumer taskConsumer = (TaskConsumer) Bootstrap.INSTANCE.getBean("helium:TaskConsumer").getBean();
			taskConsumer.putBatchStorage(QueueName.FutureMemoryTask,  new TaskQueueMemory());
			LOGGER.info("init complete:{}", QueueName.FutureMemoryTask);
		} catch (Exception e) {
			LOGGER.error("init error.{}", e);
		}
	}

	@Override
	public MessageResponse adapterNormal(MessageRequest messageRequest) {
		try {
			messageRequestTaskProducer.produce(messageRequest);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return new MessageResponse();
	}

	@Override
	public Future<MessageResponse> adapterFuture(MessageRequest messageRequest) {
		Future<MessageResponse> messageResponseFuture = new Future<>();
		messageRequest.setMessageResponseFuture(messageResponseFuture);
		messageRequestTaskProducer.produce(messageRequest);
		return messageResponseFuture;
	}

	@Override
	public MessageResponse adapterInnerFuture(MessageRequest messageRequest) {
		Future<MessageResponse> messageResponseFuture = new Future<>();
		messageRequest.setMessageResponseFuture(messageResponseFuture);
		messageRequestTaskProducer.produce(messageRequest);
		if (messageResponseFuture.await(1000)){
			LOGGER.warn("complete time out");
			return new MessageResponse();
		}
		return new MessageResponse();
	}
}
