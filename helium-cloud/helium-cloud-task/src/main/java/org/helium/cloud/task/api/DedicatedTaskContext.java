package org.helium.cloud.task.api;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * Created by Coral on 6/27/15.
 */
public final class DedicatedTaskContext {
	private static final Logger LOGGER = LoggerFactory.getLogger(DedicatedTaskContext.class);

	private String tag;
	private Map<String, DedicatedTask> tasks;
	private AtomicBoolean running = new AtomicBoolean();
	private long lastTime;

	public DedicatedTaskContext(String tag) {
		this.tag = tag;
		this.lastTime = System.currentTimeMillis();
		tasks = new HashMap<>();
	}

	public boolean isExpired(long expired) {
		return lastTime - expired < 0;
	}

	/**
	 * 获取在本context上注册的任务
	 * @param eventId
	 * @return
	 */
	public DedicatedTask getTask(String eventId) {
		synchronized (this) {
			return tasks.get(eventId);
		}
	}

	/**
	 * 在Context上添加一个任务
	 * @param task
	 * @param event
	 */
	public void putTask(DedicatedTask task, String event) {
		synchronized (this) {
			tasks.put(event, task);
		}
	}

	public void runTask(DedicatedTask task, DedicatedTaskArgs args) {
		try {
			task.processTask(this, args);
		} catch (Exception ex) {
			//
			// 如果出错则认为ctx运行完成
			setTaskRunnable();
			LOGGER.error("TaskImplementation run failed: " + task.toString() + " {}", ex);
		}
	}

	/**
	 * 移除任务
	 */
	public void close() {
		synchronized (this) {
			for (DedicatedTask task: tasks.values()) {
				try {
					task.processTaskRemoved(this);
				} catch (Exception ex) {
					LOGGER.error("task.processTaskRemoved failed:{}", ex);
				}
			}
			try {
				//TODO 获取
				//DedicatedTagManager tagManager = Spring;
				//tagManager.deleteTag(tag);
			} catch (Exception ex) {
				LOGGER.error("DedicatedTagManager.processTaskRemoved failed:{}", ex);
			}
		}
	}

	private Map<Object, Object> sessions = new ConcurrentHashMap<>();
	public void putSession(Object key, Object value) {
		sessions.put(key, value);
	}

	public Object getSession(Object key) {
		return sessions.get(key);
	}

	public void removeSession(Object key) {
		sessions.remove(key);
	}

	/**
	 *
	 * @return
	 */
	public boolean isTaskRunning() {
		return running.get();
	}

	/**
	 * 设置当前运行状态为
	 */
	public void setTaskRunnable() {
		running.set(false);
		lastTime = System.currentTimeMillis();
	}

	/**
	 * 设置当前运行状态为运行中
	 */
	public void setTaskRunning() {
		running.set(true);
	}

}
