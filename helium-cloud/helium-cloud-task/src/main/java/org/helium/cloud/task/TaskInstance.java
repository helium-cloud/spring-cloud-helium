package org.helium.cloud.task;

import org.helium.cloud.task.api.*;

import org.helium.cloud.task.manager.TaskConsumerManager;
import org.helium.perfmon.PerformanceCounterFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;

/**
 * Created by Coral on 7/28/15.
 */
public class TaskInstance  implements TaskBeanContext {

	private String eventName;
	private String storageType;

	private TaskConsumerManager consumer;
	private Class<?> argClazz;
	private TaskCounter counter;

	private Executor executor;


	private Object bean;


	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getStorageType() {
		return storageType;
	}

	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}

	public TaskConsumerManager getConsumer() {
		return consumer;
	}

	public void setConsumer(TaskConsumerManager consumer) {
		this.consumer = consumer;
	}

	public void setArgClazz(Class<?> argClazz) {
		this.argClazz = argClazz;
	}

	public void setCounter(TaskCounter counter) {
		this.counter = counter;
	}

	public TaskCounter getCounter() {
		return counter;
	}


	@Override
	public String getEventId() {
		return null;
	}

	@Override
	public String getEventName() {
		return eventName;
	}

	@Override
	public String getEventStorageType() {
		return null;
	}


	@Override
	public String toString() {
		return getEventName();
	}

	public Class<?> getArgClazz() {
		return argClazz;
	}


	public Object getBean() {
		return bean;
	}

	public void setBean(Object bean) {
		this.bean = bean;
	}


	public Executor getExecutor() {
		return executor;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	@Override
	public void consume(Object args) {
//		if (reference != null && args instanceof DedicatedTaskArgs) {
//			DedicatedTaskArgs da = (DedicatedTaskArgs)args;
//			ServerUrl urlByHash = reference.getRouter().pickServer(da.getTag().toString());
//			ServerUrl url = urlByHash;
//
//			//
//			// 尝试从TagManager中读取
//			DedicatedTagManager tagManager = null;
//			if (BeanContext.getContextService().getBean(DedicatedTagManager.ID) != null) {
//				tagManager = BeanContext.getContextService().getService(DedicatedTagManager.class);
//				//
//				// 增加特性,当存在tagManager时,允许
//				String s2 = tagManager.getOrPutTag(da.getTag(), urlByHash.toString());
//				url = ServerUrl.parse(s2);
//
//				//
//				// 如果此Server已经从全局中移除, 则将Hash取到的地址放入到TagManager中
//				if (!reference.getRouter().hasServer(url)) {
//					tagManager.putTag(da.getTag(), urlByHash.toString());
//					url = urlByHash;
//				}
//			}
//
//			TaskReference.consumeByRpc(url, eventName, getId().toString(), args);
//
//		} else {
//
//		}
		//TODO 需支持远程调用
		consumer.consume(this, args);
	}
}
