package org.helium.cloud.task.manager;

import org.helium.cloud.task.TaskBeanInstance;
import org.helium.cloud.task.api.BatchTask;
import org.helium.cloud.task.api.TaskQueue;
import org.helium.cloud.task.store.TaskArgs;
import org.helium.perfmon.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * task 消费者处理逻辑[批量增加并发处理数]
 */
public class SimpleBatchTaskConsumer extends AbstractTaskConsumer {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	/**
	 * run task返回用来区分是否存在task运行
	 *
	 * @param taskQueue
	 * @param partition
	 * @param memory
	 * @return
	 * @throws InterruptedException
	 */
	public boolean runTask(TaskQueue taskQueue, int partition, boolean memory) throws InterruptedException {
		List<TaskArgs> taskArgsList = taskQueue.poolList(partition);
		if (taskArgsList == null || taskArgsList.size() == 0) {
			return false;
		}

		//需执行task任务
		BatchTask batchTask = null;
		//task实例
		TaskBeanInstance taskInstance = null;

		//获取执行对象
		List<Object> batchArgsList = new ArrayList<>();
		for (TaskArgs taskArgs : taskArgsList) {
			if (taskInstance == null){
				taskInstance = getTaskInstance(taskArgs.getId());
			}
			if (batchTask == null && taskInstance != null){
				batchTask = (BatchTask) taskInstance.getBean();
			}

			if (batchTask != null && taskArgs.getObject() != null) {
				batchArgsList.add(taskArgs.getObject());
			}
		}
		//批量执行任务
		batchTask.processTask(batchArgsList);
		for (int i = 0;i < batchArgsList.size(); batchArgsList.size()) {
			i++;
			Stopwatch watch = taskInstance.getCounter().getConsume().begin();
			watch.end();
		}

		taskQueue.delete(partition, taskArgsList);
		return true;
	}
}

