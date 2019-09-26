package org.helium.cloud.task.rpc;

public class TaskInvokerFactoryEntity implements TaskInvokerFactory{
	private static TaskInvoker taskInvoker = null;
	@Override
	public TaskInvoker getInvoker() {
		if (taskInvoker == null){
			synchronized (this){
				if (taskInvoker == null){
					taskInvoker = new TaskInvokerImpl();
				}
			}
		}
		return taskInvoker;
	}
}
