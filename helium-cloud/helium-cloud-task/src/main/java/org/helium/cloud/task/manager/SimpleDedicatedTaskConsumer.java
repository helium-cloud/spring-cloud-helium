package org.helium.cloud.task.manager;

import com.feinno.superpojo.SuperPojoManager;
import com.feinno.superpojo.type.DateTime;
import com.feinno.superpojo.type.TimeSpan;
import org.helium.cloud.common.utils.SpringContextUtil;
import org.helium.cloud.task.TaskBeanInstance;
import org.helium.cloud.task.TaskStorageType;
import org.helium.cloud.task.api.*;
import org.helium.cloud.task.store.TaskArgs;
import org.helium.cloud.task.store.TaskQueuePriorityMemory;
import org.helium.perfmon.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * task 消费者处理逻辑
 *
 * @author wuhao
 */
public class SimpleDedicatedTaskConsumer extends AbstractTaskConsumer {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private Map<String, DedicatedTaskContext> dtContexts;

	private static final TimeSpan EXPIRED_SPAN = new TimeSpan(-15 * TimeSpan.MINUTE_MILLIS);



	/**
	 * 定时清理任务线程池
	 */
	private ScheduledExecutorService clearService = new ScheduledThreadPoolExecutor(1);


	public SimpleDedicatedTaskConsumer() {
		putStorageInner(TaskStorageType.MEMORY_TYPE, new TaskQueuePriorityMemory());
		clearService.schedule(clearTask, 30, TimeUnit.SECONDS);
		dtContexts = new ConcurrentHashMap<>();

	}

	@Override
	public boolean runTask(TaskQueue taskQueue, int partition, boolean memory) throws InterruptedException {
		//转化为innertask
		if (taskQueue instanceof TaskQueuePriority){
			return runInnerTask((TaskQueuePriority) taskQueue, partition, memory);
		}
		return false;
	}


	/**
	 * runask返回用来区分是否存在task运行
	 *
	 * @param taskQueuePriority
	 * @param partition
	 * @param memory
	 * @return
	 * @throws InterruptedException
	 */
	public boolean runInnerTask(TaskQueuePriority taskQueuePriority, int partition, boolean memory) throws InterruptedException {
		List<TaskArgs> taskArgsList = taskQueuePriority.poolList(partition);
		if (taskArgsList == null || taskArgsList.size() == 0) {
			return false;
		}
		List<TaskArgs> taskArgsListExecutor = new ArrayList<>();
		for (TaskArgs taskArgs : taskArgsList) {
			try {
				TaskBeanInstance taskInstance = getTaskInstance(taskArgs.getId());
				if (!memory) {
					taskArgs.setObject(SuperPojoManager.parsePbFrom(taskArgs.getContent(), taskInstance.getArgClazz()));
				}
				DedicatedTask dedicatedTask = (DedicatedTask) taskInstance.getBean();
				DedicatedTaskContext ctx = dtContexts.get(taskArgs.getTag());
				if (dedicatedTask != null) {
					// 如果Context为空, 表示这是一个新增的Tag, 任务可马上运行
					if (ctx == null) {
						ctx = new DedicatedTaskContext(taskArgs.getTag());
						dtContexts.put(taskArgs.getTag(), ctx);
					}
					// 如果Context正在运行, 则寻找下一个可运行的任务
					if (ctx.isTaskRunning()) {
						taskQueuePriority.putPriority(partition, taskArgs);
						continue;
					} else {
						taskArgsListExecutor.add(taskArgs);
					}
					ctx.setTaskRunning();
				} else {
					Stopwatch watch = notFounds.getConsume().begin();
					watch.fail("");
					LOGGER.error("Unknown TaskImplementation event=", taskArgs.getEvent());
				}
			} catch (Exception ex) {
				LOGGER.error("When process task for event=" + taskArgs.getEvent() + " failed {}", ex);
			}
		}
		CountDownLatch taskExecutor = new CountDownLatch(taskArgsListExecutor.size());
		for (TaskArgs taskArgs : taskArgsListExecutor) {
			try {
				TaskBeanInstance taskInstance = getTaskInstance(taskArgs.getId());
				DedicatedTask dedicatedTask = (DedicatedTask) taskInstance.getBean();
				DedicatedTaskContext ctx = dtContexts.get(taskArgs.getTag());
				Executor executor = taskInstance.getExecutor();
				if (executor == null) {
					executor = defaultExecutor;
				}
				DedicatedTaskContext finalCtx = ctx;
				executor.execute(() -> {
					Stopwatch watch = taskInstance.getCounter().getConsume().begin();
					try {
						//dttask 执行
						dedicatedTask.processTask(finalCtx, (DedicatedTaskArgs) taskArgs.getObject());
						watch.end();
					} catch (Exception ex) {
						finalCtx.setTaskRunnable();
						LOGGER.error("processTask {} failed {}", taskArgs.getEvent(), ex);
						watch.fail(ex);
					} finally {
						taskExecutor.countDown();
					}
				});
			} catch (Exception e) {
				taskExecutor.countDown();
				LOGGER.error("processTask {} failed {}", taskArgs.getEvent(), e);
			}

		}
		taskExecutor.await();
		taskQueuePriority.delete(partition, taskArgsList);
		return true;
	}


	// 由DedicatedTaskContext.putTask方法调用
	public DedicatedTaskContext putContext(String tag) {
		DedicatedTaskContext ctx = new DedicatedTaskContext(tag);
		DedicatedTaskContext old;

		//
		//如果存在全局Manager,需要通过全局进行同步
//		DedicatedTagManager tagManager = null;
//		if (BeanContext.getContextService().getBean(DedicatedTagManager.ID) != null) {
//			tagManager = BeanContext.getContextService().getService(DedicatedTagManager.class);
//			//
//			// 强制更新成本机地址
//			CentralizedService centerService = BeanContext.getContextService().getCentralizedService();
//			if (centerService != null) {
//				ServerUrl url = centerService.getServerEndpoint().getServerUrl("rpc");
//				tagManager.putTag(tag, url.toString());
//			} else {
//				throw new UnsupportedOperationException("TagManager must use with CentralizedService");
//			}
//		}

		synchronized (this) {
			old = dtContexts.put(tag, ctx);
		}
		LOGGER.info("DedicatedTask putContext tag={} old={}", tag, ctx);
		if (old != null) {
			old.close();
		}
		return ctx;
	}

	/**
	 * 移除一个context
	 *
	 * @param tag
	 */
	public void removeContext(String tag) {
		DedicatedTaskContext old;
		old = dtContexts.remove(tag);
		if (old != null) {
			old.close();
		}
	}

	/**
	 * 清理任务
	 */
	Runnable clearTask = new Runnable() {
		@Override
		public void run() {
			try {
				DateTime expired = DateTime.now().add(EXPIRED_SPAN);
				List<String> tags = new ArrayList<>();
				dtContexts.forEach((k, v) -> {
					if (v.isExpired(System.currentTimeMillis())) {
						tags.add(k);
					}
				});

				for (String tag : tags) {
					dtContexts.remove(tag);
				}
			} catch (Exception e) {
				LOGGER.error("clearTask", e);
			}
		}
	};

}

