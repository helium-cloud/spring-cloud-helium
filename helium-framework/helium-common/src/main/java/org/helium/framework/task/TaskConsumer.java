package org.helium.framework.task;

import org.helium.framework.BeanContext;
import org.helium.framework.annotations.ServiceInterface;

/**
 * Task消费者接口, 由容器实现
 */
@ServiceInterface(id = TaskBeans.TASK_CONSUMER)
public interface TaskConsumer {
	void registerTask(BeanContext ctx);

	void registerScheduledTask(BeanContext ctx);

	void registerBatchTask(BeanContext ctx);

	void registerDedicatedTask(BeanContext ctx);

	void unregisterTask(BeanContext ctx);

	void unregisterBatchTask(BeanContext ctx);
	void unregisterDedicatedTask(BeanContext ctx);

	void unregisterScheduledTask(BeanContext ctx);

	void putStorage(String stoageType, TaskQueue taskQueue);
	void putBatchStorage(String stoageType, TaskQueue taskQueue);

}
