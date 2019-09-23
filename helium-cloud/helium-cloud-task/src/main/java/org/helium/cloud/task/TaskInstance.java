package org.helium.cloud.task;

import org.helium.cloud.task.manager.TaskConsumerHandler;
import org.helium.framework.task.TaskBean;

import java.util.concurrent.Executor;

/**
 * Created by Coral on 7/28/15.
 */
public class TaskInstance implements TaskBean {


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
		TaskConsumerHandler.getInstance().consume(this, args);
	}
}
