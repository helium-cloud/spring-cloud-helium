package org.helium.cloud.task.manager;

import org.helium.cloud.task.TaskInstance;
import org.helium.cloud.task.api.TaskQueue;
import org.helium.cloud.task.api.TaskQueuePriority;
import org.helium.cloud.task.entity.TaskBeans;
import org.helium.cloud.task.api.TaskConsumer;

/**
 * Task消费者接口, 由容器实现
 */

public interface TaskConsumerManager extends TaskConsumer {


	void consume(TaskInstance task, Object args);

	TaskInstance getTaskInstance(String beanId);

	SimpleDedicatedTaskConsumer getDedicatedTaskConsumer();

	void putStorage(String stoageType, TaskQueue taskQueue);

	void putDtStorage(String stoageType, TaskQueuePriority taskQueue);

	void putBatchStorage(String stoageType, TaskQueue taskQueue);
}
