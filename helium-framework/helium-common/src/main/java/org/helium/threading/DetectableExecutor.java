package org.helium.threading;

/**
 * 可检测的线程池
 * Created by Coral on 8/31/16.
 */
public interface DetectableExecutor {
	/**
	 * worker是否busy
	 * @return
	 */
	boolean isWorkerBusy();

	/**
	 * executor是否忙
	 * @return
	 */
	boolean isQueueBusy();

	/**
	 * 获取queue的余量
	 * @return
	 */
	int getQueueSize();
}
