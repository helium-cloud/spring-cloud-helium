package org.helium.framework.task;

import org.helium.framework.annotations.ServiceInterface;

import java.util.List;

@ServiceInterface(id = TaskQueuePriority.BEAN_ID)
public interface TaskQueuePriority extends TaskQueue{
	String BEAN_ID = "helium:TaskQueuePriority";


    void putPriority(int partition, TaskArgs taskArgs);

}
