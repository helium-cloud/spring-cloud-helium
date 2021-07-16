package org.helium.cloud.task.manager;

import org.helium.cloud.task.TaskInstance;
import org.helium.cloud.task.TaskStorageType;
import org.helium.cloud.task.store.TaskQueueMemory;
import org.helium.cloud.task.utils.TaskBeanUtils;
import org.helium.framework.task.BatchTask;
import org.helium.framework.task.TaskArgs;
import org.helium.framework.task.TaskQueue;
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

	public SimpleBatchTaskConsumer() {
		//默认采用内存处理，增加内存实现
		putStorageInner(TaskStorageType.MEMORY_TYPE, new TaskQueueMemory());
	}

	/**
	 * run task返回用来区分是否存在task运行
	 *
	 * @param taskQueue
	 * @param partition
	 * @param memory
	 * @return
	 * @throws InterruptedException
	 */
	@Override
	public boolean runTask(TaskQueue taskQueue, int partition, boolean memory) throws InterruptedException {
		List<TaskArgs> taskArgsList = taskQueue.poolList(partition);
		if (taskArgsList == null || taskArgsList.size() == 0) {
			return false;
		}

		//需执行task任务
		BatchTask batchTask = null;
		//task实例
		TaskInstance taskInstance = null;

		//获取执行对象
		List<Object> batchArgsList = new ArrayList<>();
		for (TaskArgs taskArgs : taskArgsList) {
			if (taskInstance == null){
				taskInstance = TaskBeanUtils.getTaskInstance(taskArgs.getId());
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

