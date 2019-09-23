package org.helium.framework.task;

import org.helium.framework.annotations.ServiceInterface;

import java.util.List;

@ServiceInterface(id = TaskQueue.BEAN_ID)
public interface TaskQueue {
	String BEAN_ID = "helium:TaskQueue";

	void put(int partition, TaskArgs taskArgs);

	List<TaskArgs> poolList(int partition);
	TaskArgs pool(int partition);
	void delete(int partition, TaskArgs taskArgs);
	void delete(int partition, List<TaskArgs> list);
	boolean isEmpty(int partition);
}
