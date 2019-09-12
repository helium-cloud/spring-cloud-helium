package org.helium.cloud.task.api;


import org.helium.cloud.task.store.TaskArgs;

public interface TaskQueuePriority extends TaskQueue{
	String BEAN_ID = "helium:TaskQueuePriority";

    void putPriority(int partition, TaskArgs taskArgs);

}
