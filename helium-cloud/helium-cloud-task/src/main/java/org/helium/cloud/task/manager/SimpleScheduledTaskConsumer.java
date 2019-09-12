package org.helium.cloud.task.manager;

import org.helium.cloud.task.TaskInstance;

import org.helium.cloud.task.api.ScheduledTask;
import org.helium.cloud.task.api.TaskBeanContext;
import org.helium.cloud.task.store.CronExpression;
import org.helium.perfmon.Stopwatch;
import org.helium.threading.ExecutorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by Coral on 7/31/16.
 */
public class SimpleScheduledTaskConsumer {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleScheduledTaskConsumer.class);

	private static final int TASK_EXECUTOR_SIZE = 16;
	private static final int TASK_EXECUTOR_QUEUE_SIZE = 16 * 64;

	private List<ScheduledTaskNode> tasks;
	private Executor defaultExecutor;

	private Thread thread;

	public SimpleScheduledTaskConsumer() {
		tasks = new ArrayList<>();
		this.defaultExecutor = ExecutorFactory.newFixedExecutor(this.getClass().getSimpleName() +"task", TASK_EXECUTOR_SIZE, TASK_EXECUTOR_QUEUE_SIZE);

		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				SimpleScheduledTaskConsumer.this.run();
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	// @Override
	public void registerScheduledTask(TaskBeanContext bc) {
//		ScheduledTaskNode node = new ScheduledTaskNode();
//		node.id = bc.getEventId().toString();
//		String cronExpr = bc.getConfiguration().getExtension(ScheduledTask.EXTENSION_KEY_CRON);
//		try {
//			node.cron = new CronExpression(cronExpr);
//		} catch (Exception ex) {
//			throw new IllegalArgumentException("bad cron expression: " + cronExpr);
//		}
//		node.bean = (TaskInstance)bc;
//		node.task = (ScheduledTask)bc.getBean();
//		tasks.add(node);
	}

	// @Override
	public void unregisterScheduledTask(TaskBeanContext bc) {

	}

	private void run() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		while (true) {
			try {
				Thread.sleep(10);
				Date now = new Date();
				String nowText = dateFormat.format(now);    // 计算到秒

				for (ScheduledTaskNode task: tasks) {
					if (nowText.equals(task.lastRun)) {     // 每秒运行最多一次
						continue;
					}
//					if (task.cron.isSatisfiedBy(now)) {     // 午时已到
//						task.lastRun = nowText;
//						runTask(task);
//					}
				}
			} catch (InterruptedException e) {
			} catch (Throwable t) {
				LOGGER.error("SimpleTaskManager run failed: {}", t);
			}
		}
	}

	private void runTask(ScheduledTaskNode task) {
		Executor executor = task.bean.getExecutor();
		if (executor == null) {
			executor = defaultExecutor;
		}
		executor.execute(() -> {
			Stopwatch watch = new Stopwatch();
			try {
				LOGGER.info("run ScheduledTask id=" + task.id);
				task.task.processTask(null);
				LOGGER.info("run ScheduledTask id={} cost={}ms", task.id, watch.getMillseconds());
			} catch (Exception ex) {
				LOGGER.info("run ScheduledTask failed id={} cost={}ms, {}", task.id, watch.getMillseconds(), ex);
			}
		});
	}

	/**
	 * 排序规则
	 * 1. ctx正在运行的排在最后, 或不存在ctx的排在最后
	 * 2.
	 */
	private static class ScheduledTaskNode {
		String id;
		String lastRun;
		CronExpression cron;
		TaskInstance bean;
		ScheduledTask task;
	}
}
