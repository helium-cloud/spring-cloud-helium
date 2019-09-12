package org.helium.cloud.task.manager;

import org.helium.cloud.task.TaskInstance;
import org.helium.cloud.task.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TaskConsumerManagerImpl implements TaskConsumerManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskConsumerManagerImpl.class);
	private static final int TASK_RPC_CORE_SIZE = 4;
	private static final int TASK_RPC_QUEUE_SIZE = 1024;
	private Map<String, TaskInstance> tasks;

	private SimpleTaskConsumer simpleTaskConsumer;

	private SimpleBatchTaskConsumer batchTaskConsumer;

	private SimpleDedicatedTaskConsumer dedicatedTaskConsumer;

	private SimpleScheduledTaskConsumer scheduledTaskConsumer;

	public TaskConsumerManagerImpl() {
		tasks = new ConcurrentHashMap<>();
		simpleTaskConsumer = new SimpleTaskConsumer(this);
		batchTaskConsumer = new SimpleBatchTaskConsumer(this);
		dedicatedTaskConsumer = new SimpleDedicatedTaskConsumer(this);
		scheduledTaskConsumer = new SimpleScheduledTaskConsumer();
	}



	@Override
	public void consume(TaskInstance task, Object args) {
		if (task.getBean() instanceof DedicatedTask){
			dedicatedTaskConsumer.consume(task, (DedicatedTaskArgs) args);
		} else if(task.getBean() instanceof BatchTask){
			batchTaskConsumer.consume(task, args);
		} else {
			simpleTaskConsumer.consume(task, args);
		}

	}

	@Override
	public void putStorage(String stoageType, TaskQueue taskQueue) {
		simpleTaskConsumer.putStorageInner(stoageType, taskQueue);
		if(taskQueue instanceof TaskQueuePriority){

		} else if (taskQueue instanceof TaskQueue){

		}
	}

	@Override
	public void putDtStorage(String stoageType, TaskQueuePriority taskQueue) {
		dedicatedTaskConsumer.putStorageInner(stoageType, taskQueue);
	}

	@Override
	public void putBatchStorage(String stoageType, TaskQueue taskQueue) {
		batchTaskConsumer.putStorageInner(stoageType, taskQueue);
	}

	@Override
	public TaskInstance getTaskInstance(String beanId) {
		TaskInstance task = tasks.get(beanId);
		return task;
	}

	@Override
	public SimpleDedicatedTaskConsumer getDedicatedTaskConsumer() {
		return dedicatedTaskConsumer;
	}
}
