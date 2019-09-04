package org.helium.framework.spi.task;

import org.helium.framework.BeanContext;
import org.helium.framework.annotations.ServiceInterface;
import org.helium.framework.spi.TaskInstance;
import org.helium.framework.task.TaskBeans;
import org.helium.framework.task.TaskConsumer;
import org.helium.framework.task.TaskQueue;

/**
 * Task消费者接口, 由容器实现
 */
@ServiceInterface(id = TaskBeans.TASK_CONSUMER)
public interface TaskConsumerStarter extends TaskConsumer {


	void consume(TaskInstance task, Object args);

	TaskInstance getTaskInstance(String beanId);

	SimpleDedicatedTaskConsumer getDedicatedTaskConsumer();
}
