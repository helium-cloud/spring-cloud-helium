package org.helium.framework.task;

import org.helium.framework.annotations.FieldLoaderType;

/**
 * Task生产者接口,
 * 用于在用户代码中产生Task,
 * 注明仅用于注入场合
 *
 * Created by Coral on 5/5/15.
 */
@FieldLoaderType(loaderType = TaskProducerLoader.class)
public interface TaskProducer<E> {
	void produce(E args);
}
