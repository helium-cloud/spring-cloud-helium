package org.helium.cloud.task.api;


/**
 * Created by Coral on 9/12/15.
 */
public interface TaskBean {
	/**
	 * 获取事件id
	 * @return
	 */
	default String getId(){return "";};
	/**
	 * 获取事件名称
	 * @return
	 */
	default String getEvent(){return "";};

	/**
	 * 获取存储类型
	 * @return
	 */
	default String getStorage(){return "";};

	/**
	 * 消费一个args
	 * @param args
	 */
	default void consume(Object args){return;};
}
