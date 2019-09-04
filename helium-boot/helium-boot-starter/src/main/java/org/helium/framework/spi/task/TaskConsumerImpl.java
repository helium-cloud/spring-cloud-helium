package org.helium.framework.spi.task;

import org.helium.framework.BeanContext;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.spi.TaskInstance;
import org.helium.framework.task.*;
import org.helium.rpc.server.RpcServiceBootstrap;
import org.helium.threading.ExecutorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServiceImplementation(id = TaskBeans.TASK_CONSUMER)
public class TaskConsumerImpl implements TaskConsumerStarter{
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskConsumerImpl.class);
	private static final int TASK_RPC_CORE_SIZE = 4;
	private static final int TASK_RPC_QUEUE_SIZE = 1024;

	private Map<String, TaskInstance> tasks;


	private SimpleTaskConsumer simpleTaskConsumer;

	private SimpleBatchTaskConsumer batchTaskConsumer;

	private SimpleDedicatedTaskConsumer dedicatedTaskConsumer;

	private SimpleScheduledTaskConsumer scheduledTaskConsumer;

	public TaskConsumerImpl() {
		tasks = new ConcurrentHashMap<>();

		simpleTaskConsumer = new SimpleTaskConsumer(this);
		batchTaskConsumer = new SimpleBatchTaskConsumer(this);
		dedicatedTaskConsumer = new SimpleDedicatedTaskConsumer(this);
		scheduledTaskConsumer = new SimpleScheduledTaskConsumer();
		DedicatedTaskFactory.initialize(dedicatedTaskConsumer);

		//RPC消费者处理
		TaskConsumerRpcService taskConsumerRpcService = new TaskConsumerRpcService(this);
		taskConsumerRpcService.setExecutor(ExecutorFactory.newFixedExecutor("task-rpc-consumer", TASK_RPC_CORE_SIZE, TASK_RPC_QUEUE_SIZE));
		RpcServiceBootstrap.INSTANCE.registerService(taskConsumerRpcService);
	}

	@Override
	public void registerTask(BeanContext beanContext) {
		tasks.put(beanContext.getId().toString(), (TaskInstance) beanContext);
	}

	@Override
	public void registerScheduledTask(BeanContext beanContext) {
		scheduledTaskConsumer.registerScheduledTask(beanContext);
	}

	@Override
	public void registerBatchTask(BeanContext ctx) {
		tasks.put(ctx.getId().toString(), (TaskInstance) ctx);
	}

	@Override
	public void registerDedicatedTask(BeanContext beanContext) {
		tasks.put(beanContext.getId().toString(), (TaskInstance) beanContext);
	}

	@Override
	public void unregisterTask(BeanContext ctx) {

	}

	@Override
	public void unregisterBatchTask(BeanContext ctx) {

	}

	@Override
	public void unregisterDedicatedTask(BeanContext ctx) {

	}

	@Override
	public void unregisterScheduledTask(BeanContext ctx) {

	}

	@Override
	public void consume(TaskInstance task, Object args) {
		if (args instanceof DedicatedTaskArgs && task.getBean() instanceof DedicatedTask) {
			dedicatedTaskConsumer.consume(task, (DedicatedTaskArgs) args);
		} else if( task.getBean() instanceof BatchTask){
			batchTaskConsumer.consume(task, args);
		} else {
			simpleTaskConsumer.consume(task, args);
		}
	}

	@Override
	public void putStorage(String stoageType, TaskQueue taskQueue) {
		if(taskQueue instanceof TaskQueuePriority){
			dedicatedTaskConsumer.putStorageInner(stoageType, (TaskQueuePriority) taskQueue);
		} else if (taskQueue instanceof TaskQueue){
			simpleTaskConsumer.putStorageInner(stoageType, taskQueue);
		}
	}

	@Override
	public void putBatchStorage(String stoageType, TaskQueue taskQueue) {
		batchTaskConsumer.putStorageInner(stoageType, taskQueue);
	}

	@Override
	public TaskInstance getTaskInstance(String beanId) {
		TaskInstance task = tasks.get(beanId);
		return task;
	}

	@Override
	public SimpleDedicatedTaskConsumer getDedicatedTaskConsumer() {
		return dedicatedTaskConsumer;
	}
}
