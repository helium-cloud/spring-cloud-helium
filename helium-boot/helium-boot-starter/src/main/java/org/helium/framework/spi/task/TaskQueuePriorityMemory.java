package org.helium.framework.spi.task;

import org.helium.framework.task.TaskArgs;
import org.helium.framework.task.TaskQueuePriority;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class TaskQueuePriorityMemory implements TaskQueuePriority {
	private Queue<TaskArgs> queue = new PriorityBlockingQueue<>();;
	@Override
	public void put(int partition, TaskArgs taskArgs) {
		taskArgs.setPriority(0);
		queue.add(taskArgs);
	}

	@Override
	public List<TaskArgs> poolList(int partition) {
		List<TaskArgs> list = new ArrayList<>();
		for (int i = 0; i < getLimit(); i++ ){
			TaskArgs taskArgs = queue.poll();
			if (taskArgs != null){
				list.add(taskArgs);
			} else {
				break;
			}
		}
		return list;
	}

	@Override
	public void putPriority(int partition, TaskArgs taskArgs) {
		taskArgs.setPriority(taskArgs.getPriority() + 1);
		queue.add(taskArgs);
	}

	@Override
	public TaskArgs pool(int partition) {
		return queue.poll();
	}

	@Override
	public void delete(int partition, TaskArgs taskArgs) {

	}

	@Override
	public void delete(int partition, List<TaskArgs> list) {

	}

	@Override
	public boolean isEmpty(int partition) {
		return queue.isEmpty();
	}


	private int getLimit(){
		return 128;
	}

}
