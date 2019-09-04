package com.feinno.urcs.data.redis.test.task;

import com.feinno.urcs.data.redis.test.task.task.*;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.annotations.TaskEvent;
import org.helium.framework.task.TaskProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Leon on 9/10/16.
 */
@ServiceImplementation(id = "simple:AdapterService")
public class AdapterServiceImpl implements AdapterService {

	@TaskEvent(AdapterMemoryTask.TASK_EVENT)
	private TaskProducer<AdapterTaskArgs> taskProducer;

	@TaskEvent(AdapterRedisTask.TASK_EVENT)
	private TaskProducer<AdapterTaskArgs> taskProducerRedis;


	@TaskEvent(AdapterMysqlTask.TASK_EVENT)
	private TaskProducer<AdapterTaskArgs> taskProducerMysql;

	@TaskEvent(AdapterMemoryDtask.TASK_EVENT)
	private TaskProducer<AdapterTaskArgs> taskProducerDt;

	@TaskEvent(AdapterRedisDtask.TASK_EVENT)
	private TaskProducer<AdapterTaskArgs> taskProducerDtRedis;

	private static Logger LOGGER = LoggerFactory.getLogger(AdapterMemoryTask.class);

	@Override
	public void adapter(AdapterTaskArgs messageArgs) {
//		LOGGER.info("adapter:processTask{}", messageArgs.toJsonObject().toString());
//		taskProducer.produce(messageArgs);
		taskProducerDtRedis.produce(messageArgs);
//		taskProducerDt.produce(messageArgs);

	}
}
