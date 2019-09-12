package org.helium.cloud.task.api;


/**
 * Task生产者接口,
 * 用于在用户代码中产生Task,
 * 注明仅用于注入场合
 *
 * Created by Coral on 5/5/15.
 */

public interface TaskProducer<E> {
	void produce(E args);
}
