package org.helium.framework.spi.task;


import com.feinno.superpojo.type.DateTime;
import org.helium.framework.task.DedicatedTask;
import org.helium.framework.task.DedicatedTaskArgs;
import org.helium.framework.task.DedicatedTaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * Created by Coral on 6/27/15.
 */
public final class DedicatedTaskFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(DedicatedTaskFactory.class);



	private static SimpleDedicatedTaskConsumer consumer;

	public static void initialize(SimpleDedicatedTaskConsumer consumer) {
		DedicatedTaskFactory.consumer = consumer;
	}

	/**
	 * 固定Tag的任务只能在固定的服务器上被激活
	 * @param tag
	 * @return
	 */
	public static DedicatedTaskContext putTaskContext(String tag) {
		return consumer.putContext(tag);
	}

	/**
	 * 调用此Tag时仅默认此
	 * @param tag
	 */
	public static void removeTask(String tag) {
		consumer.removeContext(tag);
	}
}
