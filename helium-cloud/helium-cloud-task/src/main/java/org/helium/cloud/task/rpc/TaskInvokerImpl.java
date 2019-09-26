package org.helium.cloud.task.rpc;

import org.helium.cloud.task.TaskInstance;
import org.helium.cloud.task.manager.TaskConsumerHandler;
import org.helium.cloud.task.utils.CounterUtils;
import org.helium.cloud.task.utils.TaskBeanUtils;
import org.helium.perfmon.PerformanceCounterFactory;

public class TaskInvokerImpl implements TaskInvoker{
	@Override
	public void invoke(TaskInvokerArgs taskInvokerArgs) {
		String eventCounter = CounterUtils.getRpcEvent(taskInvokerArgs.getEvent());
		TaskRpcCounter taskRpcCounter = PerformanceCounterFactory.getCounters(TaskRpcCounter.class, eventCounter);
		taskRpcCounter.getProduce().increase();
		TaskInstance taskInstance = TaskBeanUtils.getTaskInstance(taskInvokerArgs.getEvent());
		TaskConsumerHandler.getInstance().consume(taskInstance, taskInvokerArgs.getArgs());


	}


}
