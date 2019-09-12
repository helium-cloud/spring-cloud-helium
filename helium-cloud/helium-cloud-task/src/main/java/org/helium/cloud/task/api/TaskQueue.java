package org.helium.cloud.task.api;


import org.helium.cloud.task.store.TaskArgs;

import java.util.List;

public interface TaskQueue {

	void put(int partition, TaskArgs taskArgs);
	List<TaskArgs> poolList(int partition);
	TaskArgs pool(int partition);
	void delete(int partition, TaskArgs taskArgs);
	void delete(int partition, List<TaskArgs> list);
	boolean isEmpty(int partition);
}
