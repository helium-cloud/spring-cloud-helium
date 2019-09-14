package org.helium.cloud.task.manager;

import com.feinno.superpojo.SuperPojoManager;
import org.helium.cloud.common.utils.SpringContextUtil;
import org.helium.cloud.task.TaskBeanInstance;
import org.helium.cloud.task.TaskStorageType;
import org.helium.cloud.task.api.Task;
import org.helium.cloud.task.api.TaskQueue;
import org.helium.cloud.task.store.TaskArgs;
import org.helium.cloud.task.store.TaskQueueMemory;
import org.helium.cloud.task.utils.TaskBeanUtils;
import org.helium.perfmon.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

/**
 * task 批量处理消费者
 */
public class SimpleTaskConsumer extends AbstractTaskConsumer {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	public SimpleTaskConsumer() {
		//默认采用内存处理，增加内存实现
		putStorageInner(TaskStorageType.MEMORY_TYPE, new TaskQueueMemory());
	}
	/**
	 * runask返回用来区分是否存在task运行
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
		CountDownLatch taskExecutor = null;
		if (!memory){
			taskExecutor = new CountDownLatch(taskArgsList.size());
		}

		for (TaskArgs taskArgs : taskArgsList) {
			TaskBeanInstance taskInstance = TaskBeanUtils.getTaskInstance(taskArgs.getId());
			if (!memory) {
				taskArgs.setObject(SuperPojoManager.parsePbFrom(taskArgs.getContent(), taskInstance.getArgClazz()));
			}

			Task task = (Task) taskInstance.getBean();
			if (task != null) {
				try {
					Executor executor = taskInstance.getExecutor();
					if (executor == null) {
						executor = defaultExecutor;
					}
					CountDownLatch finalTaskExecutor = taskExecutor;
					executor.execute(() -> {
						Stopwatch watch = taskInstance.getCounter().getConsume().begin();
						try {
							task.processTask(taskArgs.getObject());
							watch.end();
						} catch (Exception ex) {
							LOGGER.error("processTask {} failed {}", taskArgs.getEvent(), ex);
							watch.fail(ex);
						} finally {
							if (!memory){
								finalTaskExecutor.countDown();
							}

						}
					});
				} catch (Exception ex) {
					if (!memory){
						taskExecutor.countDown();
					}
					LOGGER.error("When process task for event=" + taskArgs.getEvent() + " failed {}", ex);
				}
			} else {
				Stopwatch watch = notFounds.getConsume().begin();
				watch.fail("");
				if (!memory){
					taskExecutor.countDown();
				}
				LOGGER.error("Unknown TaskImplementation event=", taskArgs.getEvent());
			}
		}
		if (!memory){
			taskExecutor.await();
		}
		taskQueue.delete(partition, taskArgsList);
		return true;
	}

}

