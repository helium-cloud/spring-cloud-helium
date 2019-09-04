package org.helium.framework.task;

/**
 * 专注处理针对某个特定用户、归属Id、请求的Task
 * 此Task有以下特性
 * 1.
 *
 * Created by Coral on 6/27/15.
 */
public interface DedicatedTask<E extends DedicatedTaskArgs>  {
	/**
	 *
	 * @param ctx
	 * @param args
	 * @return
	 */
	void processTask(DedicatedTaskContext ctx, E args);


	/**
	 *
	 * 当任务被移除
	 * @param ctx
	 */
	void processTaskRemoved(DedicatedTaskContext ctx);
}
