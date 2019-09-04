package org.helium.framework.spi.task;


import com.feinno.superpojo.SuperPojoManager;
import org.helium.framework.spi.TaskInstance;
import org.helium.framework.task.TaskConsumer;
import org.helium.rpc.server.RpcMethod;
import org.helium.rpc.server.RpcServerContext;
import org.helium.rpc.server.RpcServiceBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Coral on 9/16/15.
 */
public class TaskConsumerRpcService extends RpcServiceBase {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskConsumerRpcService.class);

	public static final String SERVICE_NAME = "TaskConsumerService";
	public static final String CONSUME_METHOD = "consumeArgs";
	public static final String REMOVE_TAG_METHOD = "removeTag";

	private TaskConsumerStarter consumer;

	public TaskConsumerRpcService(TaskConsumerStarter consumer) {
		super(SERVICE_NAME, true);
		this.consumer = consumer;
	}


	/**
	 * 消费Args
	 */
	@RpcMethod
	public void consumeArgs(RpcServerContext ctx) {
		RouterTaskArgs ra = ctx.getArgs(RouterTaskArgs.class);
		String eventId = ra.getEventId();
		byte[] buffer = ra.getArgsData();
		LOGGER.trace("consumeArgs event={} beanId={}", eventId, ra.getBeanId());

		TaskInstance taskInstance = consumer.getTaskInstance(ra.getBeanId());
		Object args = SuperPojoManager.parsePbFrom(buffer, taskInstance.getArgClazz());
		consumer.consume(taskInstance, args);
		ctx.end();
	}

	/**
	 * 移除一个TaskContext
	 */
	@RpcMethod
	public void removeTaskContext(RpcServerContext ctx) {
		RouterTaskArgs ra = ctx.getArgs(RouterTaskArgs.class);
		String tag = ra.getTag();
		consumer.getDedicatedTaskConsumer().removeContext(tag);
	}
}
