package org.helium.cloud.task;

import org.helium.cloud.common.utils.SpringContextUtil;
import org.helium.cloud.task.manager.TaskConsumerHandler;
import org.helium.cloud.task.rpc.TaskInvokerArgs;
import org.helium.cloud.task.rpc.TaskInvokerFactory;
import org.helium.cloud.task.rpc.TaskRpcCounter;
import org.helium.cloud.task.utils.CounterUtils;
import org.helium.framework.task.DedicatedTask;
import org.helium.framework.task.TaskBean;
import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.Stopwatch;

import java.util.UUID;
import java.util.concurrent.Executor;

/**
 * Created by Coral on 7/28/15.
 */
public class TaskInstance implements TaskBean {

	private static TaskInvokerFactory taskInvokerFactory = null;

	private String id;
	private String event;
	private String storage;

	private Class<?> argClazz;
	private Executor executor;
	private TaskCounter counter;

	private Object bean;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getStorage() {
		return storage;
	}

	public void setStorage(String storage) {
		this.storage = storage;
	}

	public Class<?> getArgClazz() {
		return argClazz;
	}

	public void setArgClazz(Class<?> argClazz) {
		this.argClazz = argClazz;
	}

	public Executor getExecutor() {
		return executor;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	public TaskCounter getCounter() {
		return counter;
	}

	public void setCounter(TaskCounter counter) {
		this.counter = counter;
	}

	public Object getBean() {
		return bean;
	}

	public void setBean(Object bean) {
		this.bean = bean;
	}

	@Override
	public void consume(Object args) {
		if (this.getBean() instanceof DedicatedTask) {
			TaskRpcCounter taskRpcCounter = PerformanceCounterFactory.getCounters(TaskRpcCounter.class, CounterUtils.getRpcEvent(event));
			Stopwatch stopwatch = taskRpcCounter.getConsume().begin();
			if (taskInvokerFactory == null){
				taskInvokerFactory = SpringContextUtil.getBean(TaskInvokerFactory.class);
			}
			TaskInvokerArgs taskInvokerArgs = new TaskInvokerArgs();
			taskInvokerArgs.setArgs(args);
			taskInvokerArgs.setEvent(event);
			taskInvokerArgs.setId(UUID.randomUUID().toString());
			taskInvokerFactory.getInvoker().invoke(taskInvokerArgs);
			stopwatch.end();
		} else {
			TaskConsumerHandler.getInstance().consume(this, args);
		}

	}
}
