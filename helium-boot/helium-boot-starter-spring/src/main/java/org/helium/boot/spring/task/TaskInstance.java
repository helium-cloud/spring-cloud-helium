package org.helium.boot.spring.task;

import org.helium.framework.BeanContext;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.entitys.BeanConfiguration;
import org.helium.framework.route.ServerUrl;
import org.helium.framework.spi.BeanInstance;
import org.helium.framework.spi.task.TaskConsumerStarter;
import org.helium.framework.spi.task.TaskCounter;
import org.helium.framework.task.*;
import org.helium.perfmon.PerformanceCounterFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Coral on 7/28/15.
 */
public class TaskInstance extends BeanInstance implements TaskBeanContext {
	private String eventName;
	private String storageType;

	private TaskConsumerStarter consumer;
	private TaskReference reference;
	private Class<?> argClazz;
	private TaskCounter counter;
	/**
	 * 构造函数
	 *
	 * @param configuration
	 * @param cp
	 */
	public TaskInstance(BeanConfiguration configuration, BeanContextProvider cp) {
		super(configuration, cp);
		this.eventName = configuration.getEvent();
		this.storageType = configuration.getStorageType();
		this.counter = PerformanceCounterFactory.getCounters(TaskCounter.class, eventName);
	}

	public TaskCounter getCounter() {
		return counter;
	}

	@Override
	protected void doResolve() {
		argClazz = getTaskArgsClass(getBean().getClass());
	}

	@Override
	public String getEventName() {
		return eventName;
	}

	@Override
	public String getStorageType() {
		return storageType;
	}


	@Override
	public String toString() {
		return getEventName();
	}

	public Class<?> getArgClazz() {
		return argClazz;
	}

	@Override
	protected void doStart() {
		consumer = BeanContext.getContextService().getService(TaskConsumerStarter.class);
		Object task = getBean();
		if (task instanceof Task<?>) {
			consumer.registerTask(this);
		} else if (task instanceof DedicatedTask<?>) {
			consumer.registerDedicatedTask(this);
		} else if (task instanceof BatchTask) {
			consumer.registerBatchTask(this);
		} else if (task instanceof ScheduledTask) {
			consumer.registerScheduledTask(this);
		} else {
			consumer.registerTask(this);
		}
	}

	@Override
	protected void doStop() {
		consumer = BeanContext.getContextService().getService(TaskConsumerStarter.class);
		Object task = getBean();
		if (task instanceof Task<?>) {
			consumer.unregisterTask(this);
		} else if (task instanceof DedicatedTask<?>) {
			consumer.unregisterDedicatedTask(this);
		} else if (task instanceof ScheduledTask) {
			consumer.unregisterScheduledTask(this);
		}
	}

	public void combineReference(TaskReference reference) {
		this.reference = reference;
		// reference.addInproc();
	}

	@Override
	public void consume(Object args) {
		if (reference != null && args instanceof DedicatedTaskArgs) {
			DedicatedTaskArgs da = (DedicatedTaskArgs)args;
			ServerUrl urlByHash = reference.getRouter().pickServer(da.getTag().toString());
			ServerUrl url = urlByHash;

			//
			// 尝试从TagManager中读取
			DedicatedTagManager tagManager = null;
			if (BeanContext.getContextService().getBean(DedicatedTagManager.ID) != null) {
				tagManager = BeanContext.getContextService().getService(DedicatedTagManager.class);
				//
				// 增加特性,当存在tagManager时,允许
				String s2 = tagManager.getOrPutTag(da.getTag(), urlByHash.toString());
				url = ServerUrl.parse(s2);

				//
				// 如果此Server已经从全局中移除, 则将Hash取到的地址放入到TagManager中
				if (!reference.getRouter().hasServer(url)) {
					tagManager.putTag(da.getTag(), urlByHash.toString());
					url = urlByHash;
				}
			}

			TaskReference.consumeByRpc(url, eventName, getId().toString(), args);

		} else {
			consumer.consume(this, args);
		}
	}

	public static Class<?> getTaskArgsClass(Class<?> clazz) {
		for (Class<?> c2 = clazz; c2 != Object.class; c2 = c2.getSuperclass()) {
			for (Type t : c2.getGenericInterfaces()) {
				if (!(t instanceof ParameterizedType)) {
					continue;
				}
				ParameterizedType pt = (ParameterizedType) t;
				if (Task.class.equals(pt.getRawType()) || DedicatedTask.class.equals(pt.getRawType())) {
					return (Class<?>) pt.getActualTypeArguments()[0];
				}
			}
		}
		return null;
	}
}
