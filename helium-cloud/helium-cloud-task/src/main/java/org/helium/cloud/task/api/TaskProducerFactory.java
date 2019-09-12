package org.helium.cloud.task.api;


/**
 * Created by Coral on 7/5/15.
 */
public interface TaskProducerFactory {
	/**
	 * 生成一个用于处理eventId的TaskProducer
	 * @return
	 */
	TaskProducer getProducer(String eventId);
}
