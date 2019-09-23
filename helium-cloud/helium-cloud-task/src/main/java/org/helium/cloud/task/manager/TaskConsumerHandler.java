package org.helium.cloud.task.manager;

import org.helium.cloud.task.TaskInstance;
import org.helium.framework.task.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TaskConsumerHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskConsumerHandler.class);


	private static TaskConsumerHandler INS = new TaskConsumerHandler();

	private Map<String, TaskInstance> tasks;

	private SimpleTaskConsumer simpleTaskConsumer;

	private SimpleBatchTaskConsumer batchTaskConsumer;

	private SimpleDedicatedTaskConsumer dedicatedTaskConsumer;

	private SimpleScheduledTaskConsumer scheduledTaskConsumer;

	private TaskConsumerHandler() {
		tasks = new ConcurrentHashMap<>();
		simpleTaskConsumer = new SimpleTaskConsumer();
		batchTaskConsumer = new SimpleBatchTaskConsumer();
		dedicatedTaskConsumer = new SimpleDedicatedTaskConsumer();
		scheduledTaskConsumer = new SimpleScheduledTaskConsumer();
	}

	public static TaskConsumerHandler getInstance(){
		return INS;
	}


	public void consume(TaskInstance task, Object args) {
		LOGGER.info("TaskConsumerHandler.consume:{}", task.getBean());
		if (task.getBean() instanceof DedicatedTask){
			dedicatedTaskConsumer.consume(task, (DedicatedTaskArgs) args);
		} else if(task.getBean() instanceof BatchTask){
			batchTaskConsumer.consume(task, args);
		} else {
			simpleTaskConsumer.consume(task, args);
		}

	}


	public void putStorage(String stoageType, TaskQueue taskQueue) {
		simpleTaskConsumer.putStorageInner(stoageType, taskQueue);
	}


	public void putDtStorage(String stoageType, TaskQueuePriority taskQueue) {
		dedicatedTaskConsumer.putStorageInner(stoageType, taskQueue);
	}


	public void putBatchStorage(String stoageType, TaskQueue taskQueue) {
		batchTaskConsumer.putStorageInner(stoageType, taskQueue);
	}


	public TaskInstance getTaskInstance(String beanId) {
		TaskInstance task = tasks.get(beanId);
		return task;
	}


	public SimpleDedicatedTaskConsumer getDedicatedTaskConsumer() {
		return dedicatedTaskConsumer;
	}
}
