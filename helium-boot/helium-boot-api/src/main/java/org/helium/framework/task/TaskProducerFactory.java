package org.helium.framework.task;

import org.helium.framework.annotations.ServiceInterface;

/**
 * Created by Coral on 7/5/15.
 */
@ServiceInterface(id = "helium:TaskProducerFactory")
public interface TaskProducerFactory {
	/**
	 * 生成一个用于处理eventId的TaskProducer
	 * @return
	 */
	TaskProducer getProducer(String eventId);
}
