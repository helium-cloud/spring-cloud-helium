package org.helium.cloud.task.api;


/**
 * Created by Coral on 9/12/15.
 */
public interface TaskBeanContext{
	/**
	 * 获取事件名称
	 * @return
	 */
	default String getEventId(){return "";};
	/**
	 * 获取事件名称
	 * @return
	 */
	default String getEventName(){return "";};

	/**
	 * 获取存储类型
	 * @return
	 */
	default String getEventStorageType(){return "";};

	/**
	 * 消费一个args
	 * @param args
	 */
	default void consume(Object args){return;};
}
