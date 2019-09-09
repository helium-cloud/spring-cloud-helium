package org.helium.framework.spi.task;

import com.feinno.superpojo.SuperPojo;
import org.helium.framework.spi.TaskInstance;
import org.helium.framework.task.*;
import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.threading.ExecutorFactory;
import org.helium.threading.FixedObservableExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

//import org.helium.framework.spi.Bootstrap;

/**
 * task 通用消费者处理逻辑
 */
public abstract class AbstractTaskConsumer {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());



	protected final int TASK_EXECUTOR_SIZE = 16;

	protected final int TASK_EXECUTOR_QUEUE_SIZE = 16 * 64;

	//线程池设定
	protected TaskCounter notFounds;

	protected TaskConsumerStarter taskConsumer;

	protected Map<String, Thread> queueThread;

	protected Map<String, TaskQueue> queueMap;

	protected FixedObservableExecutor defaultExecutor;


	public AbstractTaskConsumer(TaskConsumerStarter taskConsumer) {
		this.taskConsumer = taskConsumer;
		//默认处理线程池
		defaultExecutor = (FixedObservableExecutor) ExecutorFactory.newFixedExecutor(this.getClass().getSimpleName() +"task" , TASK_EXECUTOR_SIZE, TASK_EXECUTOR_QUEUE_SIZE);

		//Task消费处理
		queueMap = new ConcurrentHashMap<>();
		queueThread = new ConcurrentHashMap<>();
		notFounds = PerformanceCounterFactory.getCounters(TaskCounter.class, "__NOT_FOUND__");


	}


	/**
	 * 处理单队列设置
	 *
	 * @param stoageType
	 * @param taskQueue
	 */
	public void putStorageInner(String stoageType, TaskQueue taskQueue) {

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
//				while (true) {
//					try {
//
//						boolean entity = true;
//						//处理内存队列-不采用分片处理
//						if (taskQueue instanceof TaskQueueMemory || taskQueue instanceof TaskQueuePriorityMemory) {
//							if (runTask(taskQueue, 0, true)) {
//								entity = false;
//							}
//						} else {
//							List<PartitionBean> beanList = TaskConsumerAssignor.getAssignor();
//							for (PartitionBean partitionBean : beanList) {
//								if (runTask(taskQueue, partitionBean.getIndex(), false)) {
//									entity = false;
//								}
//							}
//						}
//						if (entity) {
//							Thread.sleep(500);
//						}
//					} catch (Throwable t) {
//						LOGGER.error("TaskManager run failed:", t);
//					}
//				}
			}
		});
		thread.setDaemon(true);
		thread.start();

		queueMap.put(stoageType, taskQueue);
		queueThread.put(stoageType, thread);
	}


	public void consume(TaskInstance task, Object args) {
		TaskArgs taskArgs = new TaskArgs();
		taskArgs.setEventName(task.getEventName());
		if (args instanceof SuperPojo) {
			taskArgs.setArgStr(((SuperPojo) args).toPbByteArray());
		}
		taskArgs.setObject(args);
		taskArgs.setId(task.getId().toString());
		TaskQueue queue = queueMap.get(task.getStorageType());
		if (queue == null) {
			LOGGER.warn("task.getStorageType() Must Set TaskQueue:{}. And Use MEMORY", task.getStorageType());
			queue = queueMap.get(TaskStorageType.MEMORY_TYPE);
		}
		int partition = new Random().nextInt(TaskConsumerAssignor.getPartition());
		if (args instanceof DedicatedTaskArgs){
			DedicatedTaskArgs dedicatedTaskArgs = (DedicatedTaskArgs) args;
			taskArgs.setTag(dedicatedTaskArgs.getTag());
			partition = getIntCode(TaskConsumerAssignor.getPartition(), dedicatedTaskArgs.getTag());
		}
		queue.put(partition, taskArgs);
	}
	/**
	 * 计算用户归属区块
	 *
	 * @param partition
	 * @param value
	 * @return
	 */
	public int getIntCode(int partition, String value) {
		int h = 0;
		if (h == 0 && value.length() > 0) {
			char val[] = value.toCharArray();
			for (int i = 0; i < value.length(); i++) {
				h = 31 * h + val[i];
			}
		}
		return Math.abs(h % partition);
	}
	public TaskInstance getTaskInstance(String beanId) {
		return taskConsumer.getTaskInstance(beanId);
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
	abstract public boolean runTask(TaskQueue taskQueue, int partition, boolean memory) throws InterruptedException;

}

