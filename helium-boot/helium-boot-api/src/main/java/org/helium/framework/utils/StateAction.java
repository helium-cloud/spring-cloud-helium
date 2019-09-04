package org.helium.framework.utils;

/**
 * Created by Coral on 7/25/15.
 */
public interface StateAction<E extends Enum> {
	/**
	 * 此状态是否支持此操作
	 * @param e
	 * @return
	 */
	boolean canDo(E e);

	/**
	 * 运行中的状态
	 * @return
	 */
	E getRunningState();

	/**
	 * 成功后的状态
	 * @return
	 */
	E getSuccessState();

	/**
	 * 失败后的状态
	 * @return
	 */
	E getFailedState();
}
