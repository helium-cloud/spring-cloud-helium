package org.helium.framework.task;

/**
 * 定时任务，引擎会按照，
 * 1. 按照cronExpression是否到了该执行的时候
 * 2. 执行getLocks()，默认的getLocks仅返回一个空值
 *
 * Created by Coral on 11/3/15.
 */
public interface ScheduledTask {
	String EXTENSION_KEY_CRON = "cron";
	String EXTENSION_KEY_ENABLE_REENTRY = "enableReentry";

	/**
	 * 获取Lock的Tag
	 * @return 返回本次可以执行的locks
	 */
	default Object[] getLocks() {
		return DEFAULT_LOCKS;
	}

	/**
	 * 释放一个Lock
	 */
	default void releaseLock(Object lock) {
	}

	/**
	 * 执行Task
	 * @param lock
	 */
	void processTask(Object lock);

	/**
	 * 默认的Locks
	 */
	Object[] DEFAULT_LOCKS = new Object[] { new Object() };
}
